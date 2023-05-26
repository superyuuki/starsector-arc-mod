package arc.hullmod;

public class ARCData {


    public enum Mode {
        SUPPRESSION, //up tp triple non-pd weapon range as flux decreases, disable ship system and shield, cut speed in half, blue mode
        GLIDE, //cut weapon range to 700, increase speed/manuever as flux goes up, orange mode

        //down
        BERSERK, //boost damage N%, boost timeflow, disable shields, consume soft flux to scale, red mode
    }



    public float ventAbilityCooldown = 0;
    public float ventAbilityScale = 0;

    public float modeAbilityScale = 0;
    public Mode mode = Mode.SUPPRESSION;


}
