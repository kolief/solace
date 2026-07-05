package net.solace.api.ui;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import net.solace.api.ui.TableAlignment;
import net.solace.api.ui.TableElement;

public class TableRow {
    Color rowColor;
    TableAlignment rowAlignment;
    List<TableElement> elements;

    private static List<TableElement> $default$elements() {
        return Collections.emptyList();
    }

    TableRow(Color rowColor, TableAlignment rowAlignment, List<TableElement> elements) {
        this.rowColor = rowColor;
        this.rowAlignment = rowAlignment;
        this.elements = elements;
    }

    public static TableRowBuilder builder() {
        return new TableRowBuilder();
    }

    public Color getRowColor() {
        return this.rowColor;
    }

    public TableAlignment getRowAlignment() {
        return this.rowAlignment;
    }

    public List<TableElement> getElements() {
        return this.elements;
    }

    public void setRowColor(Color rowColor) {
        this.rowColor = rowColor;
    }

    public void setRowAlignment(TableAlignment rowAlignment) {
        this.rowAlignment = rowAlignment;
    }

    public void setElements(List<TableElement> elements) {
        this.elements = elements;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TableRow)) {
            return false;
        }
        TableRow other = (TableRow)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Color this$rowColor = this.getRowColor();
        Color other$rowColor = other.getRowColor();
        if (this$rowColor == null ? other$rowColor != null : !((Object)this$rowColor).equals(other$rowColor)) {
            return false;
        }
        TableAlignment this$rowAlignment = this.getRowAlignment();
        TableAlignment other$rowAlignment = other.getRowAlignment();
        if (this$rowAlignment == null ? other$rowAlignment != null : !((Object)((Object)this$rowAlignment)).equals((Object)other$rowAlignment)) {
            return false;
        }
        List<TableElement> this$elements = this.getElements();
        List<TableElement> other$elements = other.getElements();
        return !(this$elements == null ? other$elements != null : !((Object)this$elements).equals(other$elements));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TableRow;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Color $rowColor = this.getRowColor();
        result = result * 59 + ($rowColor == null ? 43 : ((Object)$rowColor).hashCode());
        TableAlignment $rowAlignment = this.getRowAlignment();
        result = result * 59 + ($rowAlignment == null ? 43 : ((Object)((Object)$rowAlignment)).hashCode());
        List<TableElement> $elements = this.getElements();
        result = result * 59 + ($elements == null ? 43 : ((Object)$elements).hashCode());
        return result;
    }

    public String toString() {
        return "TableRow(rowColor=" + String.valueOf(this.getRowColor()) + ", rowAlignment=" + String.valueOf((Object)this.getRowAlignment()) + ", elements=" + String.valueOf(this.getElements()) + ")";
    }

    public static class TableRowBuilder {
        private Color rowColor;
        private TableAlignment rowAlignment;
        private boolean elements$set;
        private List<TableElement> elements$value;

        TableRowBuilder() {
        }

        public TableRowBuilder rowColor(Color rowColor) {
            this.rowColor = rowColor;
            return this;
        }

        public TableRowBuilder rowAlignment(TableAlignment rowAlignment) {
            this.rowAlignment = rowAlignment;
            return this;
        }

        public TableRowBuilder elements(List<TableElement> elements) {
            this.elements$value = elements;
            this.elements$set = true;
            return this;
        }

        public TableRow build() {
            List<TableElement> elements$value = this.elements$value;
            if (!this.elements$set) {
                elements$value = TableRow.$default$elements();
            }
            return new TableRow(this.rowColor, this.rowAlignment, elements$value);
        }

        public String toString() {
            return "TableRow.TableRowBuilder(rowColor=" + String.valueOf(this.rowColor) + ", rowAlignment=" + String.valueOf((Object)this.rowAlignment) + ", elements$value=" + String.valueOf(this.elements$value) + ")";
        }
    }
}

