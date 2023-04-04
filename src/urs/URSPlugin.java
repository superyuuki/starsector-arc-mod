package urs;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

public class URSPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        System.out.println("[URS] Loading Base Plugin");

        if (!Global.getSettings().getModManager().isModEnabled("MagicLib")) {
            throw new RuntimeException(
                    "[URS] Missing MagicLib, which is needed to start! \nGet it at http://fractalsoftworks.com/forum/index.php?topic=13718"
            );
        }
    }

}
