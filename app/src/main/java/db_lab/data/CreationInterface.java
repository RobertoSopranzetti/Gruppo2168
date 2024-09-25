package db_lab.data;

import java.util.Date;

public interface CreationInterface {
    int idCreation();
    Date creationDate();
    String name();
    String description();
    int strength();
    int dexterity();
    int constitution();
    int intelligence();
    int wisdom();
    int charisma();
    int downloads();
}