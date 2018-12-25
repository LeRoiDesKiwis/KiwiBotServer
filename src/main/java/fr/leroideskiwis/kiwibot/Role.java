package fr.leroideskiwis.kiwibot;

public enum Role {

    OWNER(524954057866084364L), ADMIN(524954141781655557L), MODO(524954137100681224L), TEST_MODO(527157327690989578L), MEMBER(null);


    Long id;

    Role(Long id){

        this.id = id;

    }

    public Long getId(){
        return id;
    }

}
