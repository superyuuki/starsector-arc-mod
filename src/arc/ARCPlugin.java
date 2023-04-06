package arc;

import com.fs.starfarer.api.BaseModPlugin;

public class ARCPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        System.out.println("[ARC] Loading Base Plugin");

//        if (!Global.getSettings().getModManager().isModEnabled("MagicLib")) {
//            throw new RuntimeException(
//                    "[ARC] Missing MagicLib, which is needed to start! \nGet it at http://fractalsoftworks.com/forum/index.php?topic=13718"
//            );
//        }
    }

    @Override
    public void onNewGame() {

    }
}
