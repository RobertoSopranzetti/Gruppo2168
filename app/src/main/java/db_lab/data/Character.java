package db_lab.data;

import java.util.Date;
import java.util.List;

public record Character(
        Creation creation,
        String classType,
        String race,
        int level) implements CreationInterface {
    @Override
    public String toString() {
        return Printer.stringify("Character: ", List.of(
                Printer.field("idCreation", this.creation.idCreation()),
                Printer.field("creationDate", this.creation.creationDate()),
                Printer.field("name", this.creation.name()),
                Printer.field("description", this.creation.description()),
                Printer.field("strength", this.creation.strength()),
                Printer.field("dexterity", this.creation.dexterity()),
                Printer.field("constitution", this.creation.constitution()),
                Printer.field("intelligence", this.creation.intelligence()),
                Printer.field("wisdom", this.creation.wisdom()),
                Printer.field("charisma", this.creation.charisma()),
                Printer.field("classType", this.classType),
                Printer.field("race", this.race),
                Printer.field("level", this.level)));
    }

    @Override
    public int idCreation() {
        return creation.idCreation();
    }

    @Override
    public Date creationDate() {
        return creation.creationDate();
    }

    @Override
    public String name() {
        return creation.name();
    }

    @Override
    public String description() {
        return creation.description();
    }

    @Override
    public int strength() {
        return creation.strength();
    }

    @Override
    public int dexterity() {
        return creation.dexterity();
    }

    @Override
    public int constitution() {
        return creation.constitution();
    }

    @Override
    public int intelligence() {
        return creation.intelligence();
    }

    @Override
    public int wisdom() {
        return creation.wisdom();
    }

    @Override
    public int charisma() {
        return creation.charisma();
    }
}