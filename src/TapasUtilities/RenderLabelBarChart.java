package TapasUtilities;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class RenderLabelBarChart extends JLabel_BarChart implements TableCellRenderer {
  public RenderLabelBarChart(float min, float max) {
    super(min,max);
    setOpaque(false);
  }
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    int v=((Integer)value).intValue();
    setValue(v);
    setText(""+v);
    if (isSelected)
      setBackground(table.getSelectionBackground());
    else
      setBackground(table.getBackground());
    return this;
  }
}
