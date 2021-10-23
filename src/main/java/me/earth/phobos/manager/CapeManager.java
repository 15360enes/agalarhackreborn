package me.earth.phobos.manager;

import me.earth.phobos.util.Globals;
import me.earth.phobos.util.Pair;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class CapeManager implements Globals {

    private final List<UUID> RcpCapes = new ArrayList<>();
    private final List<Pair<UUID, BufferedImage>> donatorCapes = new ArrayList<>();
    private final List<UUID> cooldudeCapes = new ArrayList<>();
    private final List<UUID> contributorCapes = new ArrayList<>();

    private final List<ResourceLocation> RcpCapeFrames = new ArrayList<>();
    private final List<ResourceLocation> coolCapeFrames = new ArrayList<>();

    public static int capeFrameCount = 0;

    static class gifCapeCounter extends TimerTask {
        @Override
        public void run() {
            capeFrameCount++;
        }
    }

    public ResourceLocation getRcpCape() {
        return RcpCapeFrames.get(capeFrameCount % 35);
    }

    public ResourceLocation getCoolCape() {
        return coolCapeFrames.get(capeFrameCount % 35);
    }

    public CapeManager() {
        Timer timer = new Timer();
        timer.schedule(new gifCapeCounter(), 0, 41);

        for (int i = 0; i < 35; i++) {
            RcpCapeFrames.add(new ResourceLocation("" + i + ".png"));
        }

        for (int i = 0; i < 37; i++) {
            coolCapeFrames.add(new ResourceLocation("textures/gifcape2/w3templateblackborder-" + i + ".png"));
        }

        try { // Rcp
            URL capesList = new URL("");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                RcpCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // dev
            URL capesList = new URL("");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                contributorCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try { // cool dudes
            URL capesList = new URL("");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                cooldudeCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // donator
            File tmp = new File("Wurstplus3"+ File.separator + "capes");
            if (!tmp.exists()) {
                tmp.mkdirs();
            }
            URL capesList = new URL("");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String colune = inputLine.trim();
                String uuid = colune.split(":")[0];
                String cape = colune.split(":")[1];
                URL capeUrl = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/capes/" + cape + ".png");
                BufferedImage capeImage = ImageIO.read(capeUrl);
                ImageIO.write(capeImage, "png", new File("Wurstplus3/capes/" + uuid + ".png"));
                donatorCapes.add(new Pair<>(UUID.fromString(uuid), capeImage));
            }
        } catch (Exception ignored) {}
    }

    public void reload(){
        try { // Rcp
            URL capesList = new URL("https://raw.githubusercontent.com/15360enes/rcpcape/main/rcpcape?token=AJYCFR6ZU3MTUUYAVD5WGS3BNXGTK");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                RcpCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // dev
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/dev.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                contributorCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // cool dudes
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/cooldude.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                cooldudeCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // donator
            File tmp = new File("Wurstplus3"+ File.separator + "capes");
            if (!tmp.exists()) {
                tmp.mkdirs();
            }
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/donator.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String colune = inputLine.trim();
                String uuid = colune.split(":")[0];
                String cape = colune.split(":")[1];
                URL capeUrl = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/capes/" + cape + ".png");
                BufferedImage capeImage = ImageIO.read(capeUrl);
                ImageIO.write(capeImage, "png", new File("Wurstplus3/capes/" + uuid + ".png"));
                donatorCapes.add(new Pair<>(UUID.fromString(uuid), capeImage));
            }
        } catch (Exception ignored) {}
    }

    public boolean isRcp(UUID uuid) {
        return this.RcpCapes.contains(uuid);
    }

    public boolean isDonator(UUID uuid) {
        for (Pair<UUID, BufferedImage> donator : this.donatorCapes) {
            if (donator.getKey().toString().equalsIgnoreCase(uuid.toString())) {
                return true;
            }
        } return false;
    }

    public BufferedImage getCapeFromDonor(UUID uuid) {
        for (Pair<UUID, BufferedImage> donator : this.donatorCapes) {
            if (donator.getKey().toString().equalsIgnoreCase(uuid.toString())) {
                return donator.getValue();
            }
        } return null;
    }

    public boolean isCool(UUID uuid) {
        return this.cooldudeCapes.contains(uuid);
    }

    public boolean isContributor(UUID uuid) {
        return this.contributorCapes.contains(uuid);
    }

}