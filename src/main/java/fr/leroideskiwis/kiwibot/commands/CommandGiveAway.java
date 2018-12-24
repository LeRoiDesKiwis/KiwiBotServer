package fr.leroideskiwis.kiwibot.commands;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.*;

public class CommandGiveAway {

    private Main main;

    public CommandGiveAway(Main main) {
        this.main = main;
    }

    @Command(name = "ginfo", description = "Avoir les infos des giveaways")
    public void gInfo(TextChannel tx) {

        tx.sendMessage("Règles : un membre aléatoirement choisi dans la liste des participants gagne à la fin de chaque partie. Vous pouvez voir la liste des participants en faisant ;gpart. Vous avez aussi plus de chances de gagner si vous invitez des gens (5 utilisations = +1 fois compté dans la liste).\n\n**Pour participer, merci de faire ;gre !**").queue();

    }

    @Command(name = "gpart", description = "Voir la liste des participants")
    public void onGPart(JDA jda, String[] args, Guild guild, TextChannel channel, Main main, Member member) {

        channel.sendMessage("Veuillez patienter... Recherche des participants...").queue();
        List<Member> members = getPoints(guild);

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.CYAN);

        List<Member> memberst = new ArrayList<>();

        int number = 0;

        for (Member participant : members) {

            int count = 0;

            if (memberst.contains(participant)) continue;

            for (Member member1 : members) {

                if (member1.equals(participant)) {
                    count++;
                    continue;
                }

            }
            int chance = (int) ((count / (double) members.size()) * 100.0);

            builder.addField(participant.getUser().getName(), chance + "% de chances de gagner !", true);


            memberst.add(participant);
            number++;

        }

        builder.setTitle("Il y a " + number + " participants.");

        channel.sendMessage(builder.build()).queue();
        channel.sendMessage("Faites " + main.getPrefixe() + "ggo confirm pour procéder au tirage au sort.").queue();

    }


    @Command(name = "gregister", description = "s'incrire au concours")
    public void onRegister(Message msg, JDA jda, Guild guild, TextChannel channel, Main main, Member member) {

        if (msg != null && member.equals(guild.getOwner()) && !msg.getMentionedMembers().isEmpty()) {

            for (Member m : msg.getMentionedMembers()) {

                onRegister(null, jda, guild, channel, main, m);

            }

        }

        if (member.getRoles().contains(jda.getRoleById(main.getConfig("concoursRole")))) {

            channel.sendMessage("Erreur : vous êtes déjà inscrit !").queue();


        } else {
            guild.getController().addSingleRoleToMember(member, jda.getRoleById(main.getConfig("concoursRole"))).queue();

            channel.sendMessage("Vous êtes maintenant inscrit au concours !").queue();

        }


    }


    @Deprecated
    public Map<Member, Integer> getInvites(Guild guild) {

        Map<Member, Integer> returnV = new HashMap<>();

        for (Invite invite : guild.getInvites().complete()) {

            try {

                Member m = guild.getMemberById(invite.getInviter().getId());

                int newI = 0;

                if (returnV.containsKey(m)) {

                    newI = returnV.get(m) + invite.getUses();
                    returnV.remove(m);

                } else newI = invite.getUses();

                returnV.put(m, newI);

            } catch (Exception e) {

                continue;

            }

        }

        return returnV;

    }

    /**
     * Count the use of all invitations created by users
     *
     * @param guild The concerned guild
     * @return A map associated all members who had create invitation with the number of uses for each of theses invitations
     */
    public Map<Member, Integer> countUseOfInvitations(@NotNull Guild guild) {
        Objects.requireNonNull(guild);

        Map<Member, Integer> invitations = new HashMap<>();

        for (Invite invite : guild.getInvites().complete()) {
            Member member = guild.getMember(invite.getInviter());
            if(member == null || !member.getRoles().contains(main.getJda().getRoleById(main.getConfig("concoursRole")))) continue;

            int totalUse = invitations.getOrDefault(member, 0);
            totalUse += invite.getUses();

            invitations.put(member, totalUse);
        }

        for(Member m : guild.getMembers()){

            if(m.getRoles().contains(main.getJda().getRoleById(main.getConfig("concoursRole")))){

                if(!invitations.containsKey(m)) invitations.put(m, 1);

            }

        }

        return invitations;
    }

    public List<Member> getPoints(Guild guild) {

        List<Member> memberPoints = new ArrayList<>();

        for (Map.Entry<Member, Integer> entry : countUseOfInvitations(guild).entrySet()) {
            Member member = entry.getKey();

            Integer invit = entry.getValue();

            int points = ((int) (1.1 * Math.log(invit) + 1));

            for (int i = 0; i < points; i++) {

                memberPoints.add(member);

            }
        }

        return memberPoints;


    }

    public Member takeWinner(@NotNull Guild guild) {
        Objects.requireNonNull(guild);

        List<Member> memberPoints = getPoints(guild);

        int randomI = new Random().nextInt(memberPoints.size());
        return memberPoints.get(randomI);
    }

    @Command(name = "ggo", description = "Tirer au sort ou voir le nombre de participants", op = true)
    public void onGo(JDA jda, String[] args, Guild guild, TextChannel channel, Main main, Member member) {

        if (args.length != 0 && args[0].equalsIgnoreCase("confirm")) {

            channel.sendMessage("Veuillez patienter... Recherche des participants...").queue();

            Map<Member, Integer> usersInvitation = countUseOfInvitations(guild);
            if (usersInvitation.isEmpty()) {
                channel.sendMessage("Il n'y a aucun participant.").queue();
                return;
            }

            Member winner = takeWinner(guild);
            channel.sendMessage(winner.getUser().getName() + " a gagné ! Bravo à lui/elle !").queue();

            for (Member member1 : usersInvitation.keySet())
                guild.getController().removeSingleRoleFromMember(member1, jda.getRoleById(main.getConfig("concoursRole"))).queue();
        } else
            channel.sendMessage("êtes-vous sûr de vouloir procéder au tirage au sort ? Faites ;ggo confirm si vous êtes sûr.").queue();
    }

}
