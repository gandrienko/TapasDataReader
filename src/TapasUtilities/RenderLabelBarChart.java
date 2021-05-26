package TapasUtilities;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class RenderLabelBarChart extends JLabel_BarChart implements TableCellRenderer {
  boolean bModeTimeOfDay=false;
  public RenderLabelBarChart(float min, float max) {
    super(min,max);
    setOpaque(false);
  }
  public void setbModeTimeOfDay() { bModeTimeOfDay=true; }
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    int v=((Integer)value).intValue();
    setValue(v);
    if (bModeTimeOfDay)
      setText(String.format("%02d",v/60)+":"+String.format("%02d",v%60));
    else
      setText(""+v);
    if (isSelected)
      setBackground(table.getSelectionBackground());
    else
      setBackground(table.getBackground());
    return this;
  }
}
