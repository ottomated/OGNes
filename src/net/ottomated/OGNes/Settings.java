package net.ottomated.OGNes;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

class Settings {

    enum Speed {
        SIX, THIRTY, SIXTY, MAX
    }

    Speed speed;
    int[] controller0;
    int[] controller1;
    String romPath;
    String tasPath;
    String savePath;
    int scale;

    Settings() throws URISyntaxException {
        speed = Speed.SIXTY;
        scale = 3;
        controller0 = new int[]{
                KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_T, KeyEvent.VK_Y
        };
        controller1 = new int[]{
                KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_SHIFT, KeyEvent.VK_ENTER, KeyEvent.VK_COMMA, KeyEvent.VK_PERIOD
        };
        romPath = Paths.get(Main.getDir(), "roms").toString();
        tasPath = Paths.get(Main.getDir(), "movies").toString();
        savePath = Paths.get(Main.getDir(), "saves").toString();
    }

    Settings(JSONObject json) {
        speed = Speed.values()[json.getInt("speed")];
        controller0 = json.getJSONArray("controller0").toList().stream().mapToInt(i -> (int) i).toArray();
        controller1 = json.getJSONArray("controller1").toList().stream().mapToInt(i -> (int) i).toArray();
        romPath = json.getString("roms");
        tasPath = json.getString("movies");
        savePath = json.getString("saves");
        scale = json.getInt("scale");
    }

    void save(File file) throws FileNotFoundException {
        JSONObject json = new JSONObject();
        json.put("speed", speed.ordinal());
        json.put("controller0", controller0);
        json.put("controller1", controller1);
        json.put("roms", romPath);
        json.put("movies", tasPath);
        json.put("saves", savePath);
        json.put("scale", scale);
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(json.toString());
        }
        try {
            new File(romPath).mkdirs();
            new File(tasPath).mkdirs();
            new File(savePath).mkdirs();
        } catch(Exception ignored) {

        }
    }
}
