package TapasUtilities;

import javax.swing.*;
import java.awt.*;

public class JLabel_ValueInSubinterval extends JLabel {
  float value, int_min, int_max, global_min, global_max;
  public JLabel_ValueInSubinterval () {
    setHorizontalAlignment(SwingConstants.RIGHT);
  }
  public void setValues (float v[]) {
    this.value=v[0];
    this.global_min=v[1];
    this.global_max=v[2];
    this.int_min=v[3];
    this.int_max=v[4];
    if (value<global_min || value>global_max)
      setText("");
    else
      if (value==Math.round(value))
        setText(Integer.valueOf((int)value).toString());
      else
        setText(""+value);
  }
  public void paint (Graphics g) {
    if (value>=global_min && value<=global_max) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
      int x1 = (int) Math.round((int_min - global_min) * getWidth() / (global_max - global_min)),
              x2 = (int) Math.round((int_max - global_min) * getWidth() / (global_max - global_min)),
              x = (int) Math.round((value - global_min) * getWidth() / (global_max - global_min));
      g.setColor(Color.lightGray);
      g.fillRect(x1, getHeight() / 2, x2 - x1, getHeight() / 2);
      g.setColor(Color.black);
      g.drawLine(x, 0, x, getHeight());
      //System.out.println("* v="+value+" in ["+int_min+".."+int_max+"] in ["+global_min+".."+global_max+"], x="+x+" in ["+x1+".."+x2+"]; width="+getWidth());
    }
    super.paint(g);
  }
}
