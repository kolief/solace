package net.solace.api.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.Text;
import net.solace.api.ui.TableAlignment;
import net.solace.api.ui.TableElement;
import net.solace.api.ui.TableRow;

public class TableComponent
implements LayoutableRenderableEntity {
    private static final TableElement EMPTY_ELEMENT = TableElement.builder().build();
    private final List<TableElement> columns = new ArrayList<TableElement>();
    private final List<TableRow> rows = new ArrayList<TableRow>();
    private final Rectangle bounds = new Rectangle();
    private TableAlignment defaultAlignment = TableAlignment.LEFT;
    private Color defaultColor = Color.WHITE;
    private Dimension gutter = new Dimension(3, 0);
    private Point preferredLocation = new Point();
    private Dimension preferredSize = new Dimension(129, 0);

    private static int getTextWidth(FontMetrics metrics, String cell) {
        return metrics.stringWidth(Text.removeTags((String)cell));
    }

    private static String[] lineBreakText(String text, int maxWidth, FontMetrics metrics) {
        String[] words = text.split(" ");
        if (words.length == 0) {
            return new String[0];
        }
        StringBuilder wrapped = new StringBuilder(words[0]);
        int spaceLeft = maxWidth - TableComponent.getTextWidth(metrics, wrapped.toString());
        for (int i = 1; i < words.length; ++i) {
            int spaceWidth;
            String word = words[i];
            int wordLen = TableComponent.getTextWidth(metrics, word);
            if (wordLen + (spaceWidth = metrics.stringWidth(" ")) > spaceLeft) {
                wrapped.append("\n").append(word);
                spaceLeft = maxWidth - wordLen;
                continue;
            }
            wrapped.append(" ").append(word);
            spaceLeft -= spaceWidth + wordLen;
        }
        return wrapped.toString().split("\n");
    }

    private static int getAlignedPosition(String str, TableAlignment alignment, int columnWidth, FontMetrics metrics) {
        int stringWidth = TableComponent.getTextWidth(metrics, str);
        int offset = 0;
        switch (alignment) {
            case LEFT: {
                break;
            }
            case CENTER: {
                offset = columnWidth / 2 - stringWidth / 2;
                break;
            }
            case RIGHT: {
                offset = columnWidth - stringWidth;
            }
        }
        return offset;
    }

    @SafeVarargs
    private static <T> T firstNonNull(T ... elements) {
        if (elements == null || elements.length == 0) {
            return null;
        }
        T cur = elements[0];
        for (int i = 0; cur == null && i < elements.length; ++i) {
            cur = elements[i];
        }
        return cur;
    }

    public Dimension render(Graphics2D graphics) {
        FontMetrics metrics = graphics.getFontMetrics();
        TableRow colRow = TableRow.builder().elements(this.columns).build();
        int[] columnWidths = this.getColumnWidths(metrics, colRow);
        graphics.translate(this.preferredLocation.x, this.preferredLocation.y);
        int height = this.displayRow(graphics, colRow, 0, columnWidths, metrics);
        for (TableRow row : this.rows) {
            height = this.displayRow(graphics, row, height, columnWidths, metrics);
        }
        graphics.translate(-this.preferredLocation.x, -this.preferredLocation.y);
        Dimension dimension = new Dimension(this.preferredSize.width, height);
        this.bounds.setLocation(this.preferredLocation);
        this.bounds.setSize(dimension);
        return dimension;
    }

    private int displayRow(Graphics2D graphics, TableRow row, int height, int[] columnWidths, FontMetrics metrics) {
        int x = 0;
        int startingRowHeight = height;
        List<TableElement> elements = row.getElements();
        for (int i = 0; i < elements.size(); ++i) {
            int y = startingRowHeight;
            TableElement cell = elements.get(i);
            String content = cell.getContent();
            if (content == null) continue;
            String[] lines = TableComponent.lineBreakText(content, columnWidths[i], metrics);
            TableAlignment alignment = this.getCellAlignment(row, i);
            Color color = this.getCellColor(row, i);
            for (String line : lines) {
                int alignmentOffset = TableComponent.getAlignedPosition(line, alignment, columnWidths[i], metrics);
                TextComponent leftLineComponent = new TextComponent();
                leftLineComponent.setPosition(new Point(x + alignmentOffset, y += metrics.getHeight()));
                leftLineComponent.setText(line);
                leftLineComponent.setColor(color);
                leftLineComponent.render(graphics);
            }
            height = Math.max(height, y);
            x += columnWidths[i] + this.gutter.width;
        }
        return height + this.gutter.height;
    }

    private int[] getColumnWidths(FontMetrics metrics, TableRow columnRow) {
        int col;
        int col2;
        int numCols = this.columns.size();
        for (TableRow r : this.rows) {
            numCols = Math.max(r.getElements().size(), numCols);
        }
        int[] maxtextw = new int[numCols];
        int[] maxwordw = new int[numCols];
        boolean[] flex = new boolean[numCols];
        boolean[] wrap = new boolean[numCols];
        int[] finalcolw = new int[numCols];
        ArrayList<TableRow> rows = new ArrayList<TableRow>(this.rows);
        rows.add(columnRow);
        for (TableRow r : rows) {
            List<TableElement> elements = r.getElements();
            for (int col3 = 0; col3 < elements.size(); ++col3) {
                TableElement ele = elements.get(col3);
                String cell = ele.getContent();
                if (cell == null) continue;
                int cellWidth = TableComponent.getTextWidth(metrics, cell);
                maxtextw[col3] = Math.max(maxtextw[col3], cellWidth);
                for (String word : cell.split(" ")) {
                    maxwordw[col3] = Math.max(maxwordw[col3], TableComponent.getTextWidth(metrics, word));
                }
                if (maxtextw[col3] != cellWidth) continue;
                wrap[col3] = cell.contains(" ");
            }
        }
        int left = this.preferredSize.width - (numCols - 1) * this.gutter.width;
        double avg = left / numCols;
        int nflex = 0;
        for (col2 = 0; col2 < numCols; ++col2) {
            double maxNonFlexLimit = 1.5 * avg;
            boolean bl = flex[col2] = (double)maxtextw[col2] > maxNonFlexLimit;
            if (flex[col2]) {
                ++nflex;
                continue;
            }
            finalcolw[col2] = maxtextw[col2];
            left -= finalcolw[col2];
        }
        if ((double)left < (double)nflex * avg) {
            for (col2 = 0; col2 < numCols; ++col2) {
                if (flex[col2] || !wrap[col2]) continue;
                left += finalcolw[col2];
                finalcolw[col2] = 0;
                flex[col2] = true;
                ++nflex;
            }
        }
        int tot = 0;
        for (col = 0; col < numCols; ++col) {
            if (!flex[col]) continue;
            maxtextw[col] = Math.min(maxtextw[col], this.preferredSize.width);
            tot += maxtextw[col];
        }
        for (col = 0; col < numCols; ++col) {
            if (!flex[col]) continue;
            finalcolw[col] = left * maxtextw[col] / tot;
            finalcolw[col] = Math.max(finalcolw[col], maxwordw[col]);
            left -= finalcolw[col];
        }
        int extraPerCol = left / numCols;
        int col4 = 0;
        while (col4 < numCols) {
            int n = col4++;
            finalcolw[n] = finalcolw[n] + extraPerCol;
            left -= extraPerCol;
        }
        int n = finalcolw.length - 1;
        finalcolw[n] = finalcolw[n] + left;
        return finalcolw;
    }

    public boolean isEmpty() {
        return this.columns.size() == 0 || this.rows.size() == 0;
    }

    private void ensureColumnSize(int size) {
        while (size > this.columns.size()) {
            this.columns.add(TableElement.builder().build());
        }
    }

    private Color getCellColor(TableRow row, int colIndex) {
        List<TableElement> rowElements = row.getElements();
        TableElement cell = colIndex < rowElements.size() ? rowElements.get(colIndex) : EMPTY_ELEMENT;
        TableElement column = colIndex < this.columns.size() ? this.columns.get(colIndex) : EMPTY_ELEMENT;
        return TableComponent.firstNonNull(cell.getColor(), row.getRowColor(), column.getColor(), this.defaultColor);
    }

    private void setColumnAlignment(int col, TableAlignment alignment) {
        assert (this.columns.size() > col);
        this.columns.get(col).setAlignment(alignment);
    }

    public void setColumnAlignments(TableAlignment ... alignments) {
        this.ensureColumnSize(alignments.length);
        for (int i = 0; i < alignments.length; ++i) {
            this.setColumnAlignment(i, alignments[i]);
        }
    }

    private TableAlignment getCellAlignment(TableRow row, int colIndex) {
        List<TableElement> rowElements = row.getElements();
        TableElement cell = colIndex < rowElements.size() ? rowElements.get(colIndex) : EMPTY_ELEMENT;
        TableElement column = colIndex < this.columns.size() ? this.columns.get(colIndex) : EMPTY_ELEMENT;
        return TableComponent.firstNonNull(cell.getAlignment(), row.getRowAlignment(), column.getAlignment(), this.defaultAlignment);
    }

    public void addRow(String ... cells) {
        ArrayList<TableElement> elements = new ArrayList<TableElement>();
        for (String cell : cells) {
            elements.add(TableElement.builder().content(cell).build());
        }
        TableRow row = TableRow.builder().build();
        row.setElements(elements);
        this.rows.add(row);
    }

    private void addRows(String[] ... rows) {
        for (String[] row : rows) {
            this.addRow(row);
        }
    }

    public void addRows(TableRow ... rows) {
        this.rows.addAll(Arrays.asList(rows));
    }

    public void setRows(String[] ... elements) {
        this.rows.clear();
        this.addRows(elements);
    }

    public void setRows(TableRow ... elements) {
        this.rows.clear();
        this.rows.addAll(Arrays.asList(elements));
    }

    private void addColumn(String col) {
        this.columns.add(TableElement.builder().content(col).build());
    }

    public void addColumns(TableElement ... columns) {
        this.columns.addAll(Arrays.asList(columns));
    }

    public void setColumns(TableElement ... elements) {
        this.columns.clear();
        this.columns.addAll(Arrays.asList(elements));
    }

    public void setColumns(String ... columns) {
        this.columns.clear();
        for (String col : columns) {
            this.addColumn(col);
        }
    }

    public void setDefaultAlignment(TableAlignment defaultAlignment) {
        this.defaultAlignment = defaultAlignment;
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setGutter(Dimension gutter) {
        this.gutter = gutter;
    }

    public void setPreferredLocation(Point preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    public List<TableElement> getColumns() {
        return this.columns;
    }

    public List<TableRow> getRows() {
        return this.rows;
    }

    public Rectangle getBounds() {
        return this.bounds;
    }
}

