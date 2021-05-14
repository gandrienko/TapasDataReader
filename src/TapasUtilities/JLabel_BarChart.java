package TapasUtilities;

import javax.swing.*;
import java.awt.*;

public class JLabel_BarChart extends JLabel {
  float min,max,v;
  String text;
  public JLabel_BarChart(float min, float max) {
    super("", Label.RIGHT);
    this.min=min;
    this.max=max;
    setHorizontalAlignment(SwingConstants.RIGHT);
  }
  public void setValue (float v) {
    this.v=v;
  }
  public void setText (String text) {
    super.setText(text);
    this.text=text;
  }
  public void paint (Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(Color.lightGray);
    g.fillRect(0,2,(int)Math.round(getWidth()*(v-min)/(max-min)),getHeight()-4);
    super.paint(g);
  }
}