package com.akira.general.datas;

public class LabWorkBuilder {
    private final LabWork lw = new LabWork();

    public LabWorkBuilder setId(Long id){ lw.setId(id); return this; }
    public LabWorkBuilder setName(String name){ lw.setName(name); return this; }
    public LabWorkBuilder setCoordinates(Coordinates c){ lw.setCoordinates(c); return this; }
    public LabWorkBuilder setCreationDate(java.util.Date d){ lw.setCreationDate(d); return this; }
    public LabWorkBuilder setMinimalPoint(Float f){ lw.setMinimalPoint(f); return this; }
    public LabWorkBuilder setMaximumPoint(long m){ lw.setMaximumPoint(m); return this; }
    public LabWorkBuilder setDescription(String desc){ lw.setDescription(desc); return this; }
    public LabWorkBuilder setDifficulty(Difficulty diff){ lw.setDifficulty(diff); return this; }
    public LabWorkBuilder setAuthor(Person p){ lw.setAuthor(p); return this; }

    public LabWork build(){ return lw; }
}
