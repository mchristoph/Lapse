package at.mchristoph.lapse.dao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class LapseDaoGenerator {
    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "at.mchristoph.lapse.dao.model");

        Entity lapseSettings = schema.addEntity("LapseSetting");
        lapseSettings.addIdProperty();
        lapseSettings.addStringProperty("name").notNull();
        lapseSettings.addStringProperty("description");
        lapseSettings.addLongProperty("framerate").notNull();
        lapseSettings.addLongProperty("interval").notNull();
        lapseSettings.addLongProperty("movie_time").notNull();
        lapseSettings.addDateProperty("created").notNull();

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }
}
