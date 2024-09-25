package db_lab.data;

import java.util.List;

public record Character(
        Creation creation,
        String classType,
        String race,
        int level) {
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
                Printer.field("downloads", this.creation.downloads()),
                Printer.field("classType", this.classType),
                Printer.field("race", this.race),
                Printer.field("level", this.level)));
    }
}