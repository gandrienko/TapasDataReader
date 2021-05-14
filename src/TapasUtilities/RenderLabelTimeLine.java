package TapasUtilities;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class RenderLabelTimeLine extends JLabel_TimeLine implements TableCellRenderer {
  public RenderLabelTimeLine(float max) {
    super(max);
    setOpaque(false);
  }
  public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    setValue((int[])value);
    if (isSelected)
      setBackground(table.getSelectionBackground());
    else
      setBackground(table.getBackground());
    return this;
  }
}
