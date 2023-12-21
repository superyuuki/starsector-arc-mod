/*
package arc.fx;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.dark.shaders.util.ShaderAPI;
import org.dark.shaders.util.ShaderLib;
import org.json.JSONException;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.json.JSONObject;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.Display;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL30;
import java.util.Set;
import org.lazywizard.lazylib.VectorUtils;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import java.nio.IntBuffer;
import java.util.Iterator;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.Map;
import java.util.List;

public class TEM_LatticeShieldShader implements ShaderAPI
{

    static final Vector2f ZERO = new Vector2f();
    static final String KEY = "ARC_Hilbert";

    final FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(4096);
    final FloatBuffer dataBufferPre = BufferUtils.createFloatBuffer(4096);
    boolean enabled;
    final int[] index = new int[7];
    int latticeTex;
    int maxHits;
    int program;
    private boolean validated;

    public TEM_LatticeShieldShader() {
        this.enabled = false;
        this.latticeTex = 0;
        this.maxHits = 100;
        this.program = 0;
        this.validated = false;
        if (!ShaderLib.areShadersAllowed() || !ShaderLib.areBuffersAllowed()) {
            this.enabled = false;
            return;
        }
        Global.getLogger(TEM_LatticeShieldShader.class).setLevel(Level.ERROR);
        try {
            this.loadSettings();
        }
        catch (IOException | JSONException ex3) {
            Global.getLogger(TEM_LatticeShieldShader.class).log((Priority)Level.ERROR, "Failed to load shader settings: " + e.getMessage());
            this.enabled = false;
            return;
        }
        if (!this.enabled) {
            return;
        }
        String vertShader;
        String fragShader;
        try {
            vertShader = Global.getSettings().loadText("data/shaders/lattice/lattice.vert");
            fragShader = Global.getSettings().loadText("data/shaders/lattice/lattice.frag");
        }
        catch (IOException ex) {
            this.enabled = false;
            return;
        }
        this.program = ShaderLib.loadShader(vertShader, fragShader);
        if (this.program == 0) {
            this.enabled = false;
            return;
        }
        GL11.glBindTexture(3552, this.latticeTex = GL11.glGenTextures());
        if (ShaderLib.useBufferCore()) {
            GL11.glTexImage1D(3552, 0, 33326, 4096, 0, 6403, 5126, (ByteBuffer)null);
        }
        else {
            GL11.glTexImage1D(3552, 0, 33326, 4096, 0, 6403, 5126, (ByteBuffer)null);
        }
        GL20.glUseProgram(this.program);
        this.index[0] = GL20.glGetUniformLocation(this.program, "tex");
        this.index[1] = GL20.glGetUniformLocation(this.program, "buf");
        this.index[2] = GL20.glGetUniformLocation(this.program, "data");
        this.index[3] = GL20.glGetUniformLocation(this.program, "trans");
        this.index[4] = GL20.glGetUniformLocation(this.program, "size");
        this.index[5] = GL20.glGetUniformLocation(this.program, "norm1");
        this.index[6] = GL20.glGetUniformLocation(this.program, "norm2");
        GL20.glUniform1i(this.index[0], 0);
        GL20.glUniform1i(this.index[1], 1);
        GL20.glUniform1i(this.index[2], 2);
        GL20.glUniform1f(this.index[3], ShaderLib.getSquareTransform());
        GL20.glUseProgram(0);
        this.enabled = true;
    }

    @Override
    public void advance(final float amount, final List<InputEventAPI> events) {
        final CombatEngineAPI engine = Global.getCombatEngine();
        LocalData localData = (LocalData) engine.getCustomData().get(KEY);
        final Map<Object, LatticeHit> hits = localData.hits;
        if (!engine.isPaused()) {
            final Iterator<Map.Entry<Object, LatticeHit>> iter = hits.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Object, LatticeHit> entry = iter.next();
                LatticeHit hit = entry.getValue();
                float mult = 1.0f;
                if (hit.attachment.getAnchor() instanceof ShipAPI) {
                    mult = ((ShipAPI)hit.attachment.getAnchor()).getMutableStats().getTimeMult().getModifiedValue();
                }
                hit.life += amount * mult;
                if (hit.life >= hit.lifetime) {
                    iter.remove();
                }
            }
        }
    }

    @Override
    public void destroy() {
        if (this.program != 0) {
            final ByteBuffer countbb = ByteBuffer.allocateDirect(4);
            final ByteBuffer shadersbb = ByteBuffer.allocateDirect(8);
            final IntBuffer count = countbb.asIntBuffer();
            final IntBuffer shaders = shadersbb.asIntBuffer();
            GL20.glGetAttachedShaders(this.program, count, shaders);
            for (int i = 0; i < 2; ++i) {
                GL20.glDeleteShader(shaders.get());
            }
            GL20.glDeleteProgram(this.program);
        }
        if (this.latticeTex != 0) {
            GL11.glDeleteTextures(this.latticeTex);
        }
    }

    @Override
    public ShaderAPI.RenderOrder getRenderOrder() {
        return ShaderAPI.RenderOrder.WORLD_SPACE;
    }

    @Override
    public void initCombat() {
        Global.getCombatEngine().getCustomData().put("TEM_LatticeShieldShader", new LocalData());
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void renderInScreenCoords(final ViewportAPI viewport) {
    }

    @Override
    public void renderInWorldCoords(final ViewportAPI viewport) {
        CombatEngineAPI engine = Global.getCombatEngine();
        int objectCount = 0;
        int systemCount = 0;
        List<ShipAPI> ships = engine.getShips();
        for (int listSize = ships.size(), i = 0; i < listSize; ++i) {
            final ShipAPI ship = ships.get(i);
            if (ship.getVariant().getHullMods().contains("tem_latticeshield")) {
                if (TEM_AegisShieldStats.effectLevel(ship) > 0.0f) {
                    ++systemCount;
                }
                ++objectCount;
            }
        }
        if (objectCount <= 0) {
            return;
        }
        LocalData localData = (LocalData) engine.getCustomData().get(KEY);
        final Set<BeamAPI> beams = localData.beams;
        final Map<Object, LatticeHit> hits = localData.hits;
        final Map<DamagingProjectileAPI, Float> projectiles = localData.projectiles;
        final List<DamagingProjectileAPI> allProjectiles = engine.getProjectiles();
        for (int listSize = allProjectiles.size(), j = 0; j < listSize; ++j) {
            final DamagingProjectileAPI proj = allProjectiles.get(j);
            if (!proj.didDamage()) {
                if (!projectiles.containsKey(proj)) {
                    projectiles.put(proj, proj.getDamageAmount());
                }
            }
        }
        final Iterator<Map.Entry<DamagingProjectileAPI, Float>> iter1 = projectiles.entrySet().iterator();
        while (iter1.hasNext()) {
            final Map.Entry<DamagingProjectileAPI, Float> entry = iter1.next();
            final DamagingProjectileAPI proj2 = entry.getKey();
            final float baseDamage = entry.getValue();
            if (Math.random() > 0.8) {
                entry.setValue(Math.max(baseDamage, proj2.getDamageAmount()));
            }
            if (proj2.didDamage()) {
                if (proj2.getDamageTarget() instanceof ShipAPI) {
                    final ShipAPI ship2 = (ShipAPI)proj2.getDamageTarget();
                    if (ship2.getVariant().getHullMods().contains("tem_latticeshield")) {
                        final float damageReduction = TEM_LatticeShield.shieldLevel(ship2);
                        if (damageReduction > 0.0f) {
                            float damage = baseDamage;
                            if (proj2.getDamageType() == DamageType.KINETIC) {
                                damage *= 1.25f;
                            }
                            if (proj2.getDamageType() == DamageType.HIGH_EXPLOSIVE) {
                                damage *= 0.9f;
                            }
                            if (proj2.getDamageType() == DamageType.FRAGMENTATION) {
                                damage *= 0.63f;
                            }
                            float soundDamage = baseDamage;
                            if (proj2.getDamageType() == DamageType.KINETIC) {
                                soundDamage *= 1.5f;
                            }
                            if (proj2.getDamageType() == DamageType.HIGH_EXPLOSIVE) {
                                soundDamage *= 0.67f;
                            }
                            if (proj2.getDamageType() == DamageType.FRAGMENTATION) {
                                soundDamage *= 0.4f;
                            }
                            float fader = 1.0f;
                            if (!(proj2 instanceof MissileAPI) && proj2.getWeapon() != null) {
                                fader = Math.max(1.0f - Math.max(proj2.getElapsed() / (proj2.getWeapon().getRange() / proj2.getWeapon().getProjectileSpeed()) - 1.0f, 0.0f) / (400.0f / proj2.getWeapon().getProjectileSpeed()), 0.25f);
                            }
                            final float factor = ship2.getMutableStats().getShieldDamageTakenMult().getModifiedValue();
                            damage *= fader * factor;
                            float volume = 0.7f;
                            if (soundDamage >= 200.0f) {
                                Global.getSoundPlayer().playSound("tem_latticeshield_heavy", 1.0f, volume, proj2.getLocation(), ship2.getVelocity());
                            }
                            else if (soundDamage >= 70.0f) {
                                Global.getSoundPlayer().playSound("tem_latticeshield_solid", 1.0f, volume, proj2.getLocation(), ship2.getVelocity());
                            }
                            else {
                                volume = Math.max(Math.min(volume * soundDamage / 70.0f, 1.0f), 0.35f);
                                Global.getSoundPlayer().playSound("tem_latticeshield_light", 1.0f, volume, proj2.getLocation(), ship2.getVelocity());
                            }
                            if (damage > 25.0f) {
                                final float lifetime = (float)Math.pow(damage, 0.25) * 0.3f * damageReduction;
                                final float size = (float)Math.pow(damage, 0.33) * 35.0f;
                                final float intensity = (float)Math.pow(damage, 0.33) * 0.35f * damageReduction;
                                hits.put(proj2, new LatticeHit(new AnchoredEntity((CombatEntityAPI)ship2, proj2.getLocation()), size, lifetime, intensity, (BeamAPI)null));
                                final Vector2f tempVec = new Vector2f();
                                final float baseAngle = VectorUtils.getAngle(ship2.getLocation(), proj2.getLocation());
                                for (int limit = (int)size / 20, k = 0; k < limit; ++k) {
                                    tempVec.set(intensity * 50.0f * ((float)Math.random() + 1.0f), 0.0f);
                                    float angle = baseAngle + ((float)Math.random() - 0.5f) * 225.0f;
                                    if (angle >= 360.0f) {
                                        angle -= 360.0f;
                                    }
                                    else if (angle < 0.0f) {
                                        angle += 360.0f;
                                    }
                                    VectorUtils.rotate(tempVec, angle, tempVec);
                                    Vector2f.add(tempVec, ship2.getVelocity(), tempVec);
                                    engine.addHitParticle(proj2.getLocation(), tempVec, (float)Math.random() * 5.0f + 5.0f, intensity, lifetime * ((float)Math.random() + 1.0f) / 4.0f, TEM_LatticeShield.VISUAL_SHIELD_COLOR);
                                }
                            }
                        }
                    }
                }
                iter1.remove();
            }
            else {
                if (engine.isEntityInPlay(proj2)) {
                    continue;
                }
                iter1.remove();
            }
        }
        final List<BeamAPI> allBeams = engine.getBeams();
        for (int listSize = allBeams.size(), l = 0; l < listSize; ++l) {
            BeamAPI beam = allBeams.get(l);
            if (beam.getBrightness() > 0.0f) {
                beams.add(beam);
            }
        }
        final Iterator<BeamAPI> iter2 = beams.iterator();
        while (iter2.hasNext()) {
            final BeamAPI beam = iter2.next();
            if (beam.getDamageTarget() instanceof ShipAPI) {
                final ShipAPI ship2 = (ShipAPI)beam.getDamageTarget();
                if (ship2.getVariant().getHullMods().contains("tem_latticeshield")) {
                    final float damageReduction = TEM_LatticeShield.shieldLevel(ship2);
                    if (damageReduction > 0.0f) {
                        float damage = beam.getWeapon().getDerivedStats().getDps() * beam.getBrightness();
                        if (beam.getWeapon().getDamageType() == DamageType.FRAGMENTATION) {
                            damage *= 0.63f;
                        }
                        if (beam.getWeapon().getDamageType() == DamageType.HIGH_EXPLOSIVE) {
                            damage *= 0.83f;
                        }
                        if (beam.getWeapon().getDamageType() == DamageType.KINETIC) {
                            damage *= 1.25f;
                        }
                        if (damage > 0.0f) {
                            final float lifetime2 = (float)Math.pow(damage, 0.25) * 0.3f * damageReduction;
                            final float size2 = (float)Math.pow(damage, 0.33) * 25.0f;
                            final float intensity2 = (float)Math.pow(damage, 0.33) * 0.25f * damageReduction;
                            final LatticeHit existingHit = hits.get(beam);
                            if (existingHit != null) {
                                existingHit.attachment.reanchor((CombatEntityAPI)ship2, beam.getTo());
                                existingHit.size = size2;
                                existingHit.lifetime = lifetime2;
                                existingHit.life = 0.0f;
                                existingHit.intensity = intensity2;
                            }
                            else {
                                hits.put(beam, new LatticeHit(new AnchoredEntity((CombatEntityAPI)ship2, beam.getTo()), size2, lifetime2, intensity2, beam));
                            }
                        }
                    }
                }
            }
            if (beam.getBrightness() <= 0.0f || (Math.random() > 0.9 && !allBeams.contains(beam))) {
                iter2.remove();
            }
        }
        if (!this.enabled) {
            return;
        }



        //shader
        if (!hits.isEmpty() || systemCount > 0) {
            final CombatEngineAPI engine1 = Global.getCombatEngine();
            final LocalData localData1 = (LocalData) engine1.getCustomData().get("TEM_LatticeShieldShader");
            final Map<Object, LatticeHit> hits1 = localData1.hits;
            GL11.glPushAttrib(1048575);
            if (ShaderLib.useBufferCore()) {
                GL30.glBindFramebuffer(36160, ShaderLib.getAuxiliaryBufferId());
            }
            else if (ShaderLib.useBufferARB()) {
                ARBFramebufferObject.glBindFramebuffer(36160, ShaderLib.getAuxiliaryBufferId());
            }
            else {
                EXTFramebufferObject.glBindFramebufferEXT(36160, ShaderLib.getAuxiliaryBufferId());
            }
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(viewport.getLLX(), (double)(viewport.getLLX() + viewport.getVisibleWidth()), (double) viewport.getLLY(), (double)(viewport.getLLY() + viewport.getVisibleHeight()), -2000.0, 2000.0);
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glColorMask(true, true, true, true);
            GL11.glClear(16384);
            int objectCount1 = 0;
            int systemCount1 = 0;
            final List<ShipAPI> ships1 = (List<ShipAPI>) engine1.getShips();
            for (int listSize = ships1.size(), i = 0; i < listSize; ++i) {
                final ShipAPI ship = ships1.get(i);
                if (ship.getVariant().getHullMods().contains("tem_latticeshield")) {
                    final Vector2f shipLocation = new Vector2f(ship.getLocation());
                    if (ShaderLib.isOnScreen(shipLocation, 1.25f * ship.getCollisionRadius())) {
                        final String nonDHullId = TEM_Util.getNonDHullId(ship.getHullSpec());
                        SpriteAPI sprite  = null;
                        switch (nonDHullId) {
                            case "tem_teuton": {
                                sprite = Global.getSettings().getSprite("latticeShield", "tem_teuton");
                                break;
                            }
                            case "tem_jesuit": {
                                sprite = Global.getSettings().getSprite("latticeShield", "tem_jesuit");
                                break;
                            }
                            case "tem_martyr": {
                                sprite = Global.getSettings().getSprite("latticeShield", "tem_martyr");
                                break;
                            }
                            case "tem_crusader": {
                                sprite = Global.getSettings().getSprite("latticeShield", "tem_crusader");
                                break;
                            }
                            case "tem_paladin": {
                                sprite = Global.getSettings().getSprite("latticeShield", "tem_paladin");
                                break;
                            }
                            case "tem_chevalier": {
                                sprite = Global.getSettings().getSprite("latticeShield", "tem_chevalier");
                                break;
                            }
                            case "tem_archbishop": {
                                sprite = Global.getSettings().getSprite("latticeShield", "tem_archbishop");
                                break;
                            }
                            case "tem_boss_paladin": {
                                sprite = Global.getSettings().getSprite("latticeShield", "tem_boss_paladin");
                                break;
                            }
                            case "tem_boss_archbishop": {
                                sprite = Global.getSettings().getSprite("latticeShield", "tem_boss_archbishop");
                                break;
                            }
                            default: {
                                continue;
                            }
                        }
                        final float aegis = 1f;
                        sprite.setAngle(ship.getFacing() - 90.0f);
                        sprite.setCenter(ship.getSpriteAPI().getCenterX(), ship.getSpriteAPI().getCenterY());
                        ++systemCount1;
                        sprite.setColor(Color.ORANGE);
                        sprite.setAlphaMult(aegis * ((float)Math.random() * 0.1f + 0.65f));
                        sprite.setBlendFunc(1, 1);
                        sprite.renderAtCenter(shipLocation.x, shipLocation.y);
                        ++objectCount1;
                    }
                }
            }
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5890);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
            if (ShaderLib.useBufferCore()) {
                GL30.glBindFramebuffer(36160, 0);
            }
            else if (ShaderLib.useBufferARB()) {
                ARBFramebufferObject.glBindFramebuffer(36160, 0);
            }
            else {
                EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
            }
            GL11.glPopAttrib();
            GL11.glViewport(0, 0, (int)(Global.getSettings().getScreenWidth() * Display.getPixelScaleFactor()), (int)(Global.getSettings().getScreenHeight() * Display.getPixelScaleFactor()));
            if (objectCount1 <= 0 && systemCount1 <= 0) {
                return;
            }
            ShaderLib.beginDraw(this.program);
            Vector2f maxCoords = null;
            Vector2f minCoords = null;
            float maxSize = 0.0f;
            float maxIntensity = 0.0f;
            int hitCount = 0;
            final float[] bufferPut = new float[4];
            for (final LatticeHit hit2 : hits1.values()) {
                float size = Math.max(hit2.size * (hit2.lifetime - hit2.life * 0.5f) / hit2.lifetime, 0.0f);
                if (!ShaderLib.isOnScreen(hit2.attachment.getLocation(), size)) {
                    continue;
                }
                final Vector2f coords = ShaderLib.transformScreenToUV(ShaderLib.transformWorldToScreen(hit2.attachment.getLocation()));
                size = ShaderLib.unitsToUV(size);
                final float intensity = Math.max(hit2.intensity * (hit2.lifetime - hit2.life) / hit2.lifetime, 0.0f);
                if (maxCoords == null || minCoords == null) {
                    maxCoords = new Vector2f(coords);
                    minCoords = new Vector2f(coords);
                }
                else {
                    if (coords.x > maxCoords.x) {
                        maxCoords.x = coords.x;
                    }
                    else if (coords.x < minCoords.x) {
                        minCoords.x = coords.x;
                    }
                    if (coords.y > maxCoords.y) {
                        maxCoords.y = coords.y;
                    }
                    else if (coords.y < minCoords.y) {
                        minCoords.y = coords.y;
                    }
                }
                if (size > maxSize) {
                    maxSize = size;
                }
                if (intensity > maxIntensity) {
                    maxIntensity = intensity;
                }
                bufferPut[0] = coords.x;
                bufferPut[1] = coords.y;
                bufferPut[2] = size;
                bufferPut[3] = intensity;
                this.dataBufferPre.put(bufferPut);
                if (++hitCount >= Math.min(1024, this.maxHits)) {
                    break;
                }
            }
            Vector2f normX;
            Vector2f normY;
            Vector2f normS;
            Vector2f normI;
            if (hitCount <= 0 || minCoords == null || maxCoords == null) {
                normX = TEM_LatticeShieldShader.ZERO;
                normY = TEM_LatticeShieldShader.ZERO;
                normS = TEM_LatticeShieldShader.ZERO;
                normI = TEM_LatticeShieldShader.ZERO;
                hitCount = 0;
            }
            else {
                normX = ShaderLib.getTextureDataNormalization(minCoords.x, maxCoords.x);
                normY = ShaderLib.getTextureDataNormalization(minCoords.y, maxCoords.y);
                normS = ShaderLib.getTextureDataNormalization(0.0f, maxSize);
                normI = ShaderLib.getTextureDataNormalization(0.0f, maxIntensity);
                this.dataBufferPre.flip();
                for (int size2 = hitCount * 4, j = 0; j < size2; ++j) {
                    final int pos = j % 4;
                    if (pos == 0) {
                        this.dataBuffer.put((this.dataBufferPre.get() - normX.y) / normX.x);
                    }
                    else if (pos == 1) {
                        this.dataBuffer.put((this.dataBufferPre.get() - normY.y) / normY.x);
                    }
                    else if (pos == 2) {
                        this.dataBuffer.put(this.dataBufferPre.get() / normS.x);
                    }
                    else {
                        this.dataBuffer.put(this.dataBufferPre.get() / normI.x);
                    }
                }
            }
            this.dataBuffer.flip();
            GL11.glBindTexture(3552, this.latticeTex);
            GL11.glTexSubImage1D(3552, 0, 0, this.dataBuffer.remaining(), 6403, 5126, this.dataBuffer);
            GL11.glTexParameteri(3552, 10241, 9728);
            GL11.glTexParameteri(3552, 10240, 9728);
            GL11.glTexParameteri(3552, 10242, 10496);
            GL11.glTexParameteri(3552, 10243, 10496);
            GL20.glUniform1i(this.index[4], hitCount);
            GL20.glUniform4f(this.index[5], normX.x, normX.y, normY.x, normY.y);
            GL20.glUniform2f(this.index[6], normS.x, normI.x);3
            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, ShaderLib.getScreenTexture());
            GL13.glActiveTexture(33985);
            GL11.glBindTexture(3553, ShaderLib.getAuxiliaryBufferTexture());
            GL13.glActiveTexture(33986);
            GL11.glBindTexture(3552, this.latticeTex);
            if (!this.validated) {
                this.validated = true;
                GL20.glValidateProgram(this.program);
                if (GL20.glGetProgrami(this.program, 35715) == 0) {
                    Global.getLogger(ShaderLib.class).log(Level.ERROR, ShaderLib.getProgramLogInfo(this.program));
                    ShaderLib.exitDraw();
                    this.dataBuffer.clear();
                    this.dataBufferPre.clear();
                    this.enabled = false;
                    return;
                }
            }
            GL11.glDisable(3042);
            ShaderLib.screenDraw(ShaderLib.getScreenTexture(), 33984);
            ShaderLib.exitDraw();
            this.dataBuffer.clear();
            this.dataBufferPre.clear();
        }
    }

    private void loadSettings() throws IOException, JSONException {
        final JSONObject settings = Global.getSettings().loadJSON("TEMPLAR_OPTIONS.ini");
        this.enabled = settings.getBoolean("enableShieldOverlay");
        this.maxHits = settings.getInt("maximumShieldHits");
    }

    static final class LatticeHit
    {
        final AnchoredEntity attachment;
        final BeamAPI beam;
        float intensity;
        float life;
        float lifetime;
        float size;

        private LatticeHit(final AnchoredEntity attachment, final float size, final float lifetime, final float intensity, final BeamAPI beam) {
            this.life = 0.0f;
            this.lifetime = lifetime;
            this.size = size;
            this.attachment = attachment;
            this.intensity = intensity;
            this.beam = beam;
        }
    }

    static final class LocalData
    {
        final Set<BeamAPI> beams;
        final Map<Object, LatticeHit> hits;
        final Map<DamagingProjectileAPI, Float> projectiles;

        private LocalData() {
            this.beams = new LinkedHashSet<>(200);
            this.hits = new LinkedHashMap<>(200);
            this.projectiles = new LinkedHashMap<>(2000);
        }
    }
}
*/
