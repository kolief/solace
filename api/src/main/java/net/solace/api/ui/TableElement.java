package net.solace.api.ui;

import java.awt.Color;
import net.solace.api.ui.TableAlignment;

public class TableElement {
    TableAlignment alignment;
    Color color;
    String content;

    TableElement(TableAlignment alignment, Color color, String content) {
        this.alignment = alignment;
        this.color = color;
        this.content = content;
    }

    public static TableElementBuilder builder() {
        return new TableElementBuilder();
    }

    public TableAlignment getAlignment() {
        return this.alignment;
    }

    public Color getColor() {
        return this.color;
    }

    public String getContent() {
        return this.content;
    }

    public void setAlignment(TableAlignment alignment) {
        this.alignment = alignment;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TableElement)) {
            return false;
        }
        TableElement other = (TableElement)o;
        if (!other.canEqual(this)) {
            return false;
        }
        TableAlignment this$alignment = this.getAlignment();
        TableAlignment other$alignment = other.getAlignment();
        if (this$alignment == null ? other$alignment != null : !((Object)((Object)this$alignment)).equals((Object)other$alignment)) {
            return false;
        }
        Color this$color = this.getColor();
        Color other$color = other.getColor();
        if (this$color == null ? other$color != null : !((Object)this$color).equals(other$color)) {
            return false;
        }
        String this$content = this.getContent();
        String other$content = other.getContent();
        return !(this$content == null ? other$content != null : !this$content.equals(other$content));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TableElement;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        TableAlignment $alignment = this.getAlignment();
        result = result * 59 + ($alignment == null ? 43 : ((Object)((Object)$alignment)).hashCode());
        Color $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : ((Object)$color).hashCode());
        String $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : $content.hashCode());
        return result;
    }

    public String toString() {
        return "TableElement(alignment=" + String.valueOf((Object)this.getAlignment()) + ", color=" + String.valueOf(this.getColor()) + ", content=" + this.getContent() + ")";
    }

    public static class TableElementBuilder {
        private TableAlignment alignment;
        private Color color;
        private String content;

        TableElementBuilder() {
        }

        public TableElementBuilder alignment(TableAlignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public TableElementBuilder color(Color color) {
            this.color = color;
            return this;
        }

        public TableElementBuilder content(String content) {
            this.content = content;
            return this;
        }

        public TableElement build() {
            return new TableElement(this.alignment, this.color, this.content);
        }

        public String toString() {
            return "TableElement.TableElementBuilder(alignment=" + String.valueOf((Object)this.alignment) + ", color=" + String.valueOf(this.color) + ", content=" + this.content + ")";
        }
    }
}

