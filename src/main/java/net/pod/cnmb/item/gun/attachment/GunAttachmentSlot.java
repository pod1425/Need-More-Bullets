package net.pod.cnmb.item.gun.attachment;

public enum GunAttachmentSlot {
    SCOPE("attachment.cnmb.scope"),
    BARREL("attachment.cnmb.barrel"),
    MAGAZINE("attachment.cnmb.magazine"),
    TRIGGER("attachment.cnmb.trigger"),
    HANDLE("attachment.cnmb.handle"),
    UNDERSIDE("attachment.cnmb.underside"),
    MISC1("attachment.cnmb.misc1"),
    MISC2("attachment.cnmb.misc2"),
    MISC3("attachment.cnmb.misc3"),
    MISC4("attachment.cnmb.misc4"),
    MISC5("attachment.cnmb.misc5"),
    MISC6("attachment.cnmb.misc6");

    public static final int COUNT = values().length;
    private final String translatationKey;


    GunAttachmentSlot(String name) {
        this.translatationKey = name;
    }

    public String getTranslationKey() {
        return translatationKey;
    }
}
