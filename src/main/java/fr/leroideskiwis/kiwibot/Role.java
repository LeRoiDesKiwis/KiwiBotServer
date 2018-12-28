package fr.leroideskiwis.kiwibot;

public enum Role {

    MEMBER(null),
    TEST_MODO(527157327690989578L),
    MODO(524954137100681224L),
    ADMIN(524954141781655557L),
    OWNER(524954057866084364L);


    Long id;

    Role(Long id){

        this.id = id;

    }

    public Long getId(){
        return id;
    }

}
