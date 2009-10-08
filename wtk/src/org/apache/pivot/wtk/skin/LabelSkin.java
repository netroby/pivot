/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.wtk.skin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import org.apache.pivot.collections.Dictionary;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.GraphicsUtilities;
import org.apache.pivot.wtk.HorizontalAlignment;
import org.apache.pivot.wtk.Insets;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.LabelListener;
import org.apache.pivot.wtk.Platform;
import org.apache.pivot.wtk.TextDecoration;
import org.apache.pivot.wtk.Theme;
import org.apache.pivot.wtk.VerticalAlignment;


/**
 * Label skin.
 * <p>
 * TODO showEllipsis style
 * <p>
 * TODO breakOnWhitespaceOnly style
 */
public class LabelSkin extends ComponentSkin implements LabelListener {
    private FontRenderContext fontRenderContext = new FontRenderContext(null, true, true);

    private Font font;
    private Color color;
    private Color backgroundColor;
    private TextDecoration textDecoration;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private Insets padding;
    private boolean wrapText;

    public LabelSkin() {
        Theme theme = Theme.getTheme();
        font = theme.getFont();
        color = Color.BLACK;
        backgroundColor = null;
        textDecoration = null;
        horizontalAlignment = HorizontalAlignment.LEFT;
        verticalAlignment = VerticalAlignment.TOP;
        padding = Insets.NONE;
        wrapText = false;
    }

    @Override
    public void install(Component component) {
        super.install(component);

        Label label = (Label)getComponent();
        label.getLabelListeners().add(this);
    }

    @Override
    public int getPreferredWidth(int height) {
        int preferredWidth = 0;

        Label label = (Label)getComponent();
        String text = label.getText();

        if (text != null
            && text.length() > 0) {
            Rectangle2D stringBounds = font.getStringBounds(text, fontRenderContext);
            preferredWidth = (int)Math.ceil(stringBounds.getWidth());
        }

        preferredWidth += (padding.left + padding.right);

        return preferredWidth;
    }

    @Override
    public int getPreferredHeight(int width) {
        int preferredHeight = 0;

        Label label = (Label)getComponent();
        String text = label.getText();

        if (text == null) {
            text = "";
        }

        if (wrapText
            && width != -1
            && text.length() > 0) {
            int contentWidth = width - (padding.left + padding.right);

            AttributedString attributedText = new AttributedString(text);
            attributedText.addAttribute(TextAttribute.FONT, font);

            AttributedCharacterIterator aci = attributedText.getIterator();
            LineBreakMeasurer lbm = new LineBreakMeasurer(aci, fontRenderContext);

            float lineHeights = 0;
            while (lbm.getPosition() < aci.getEndIndex()) {
                int offset = lbm.nextOffset(contentWidth);

                LineMetrics lm = font.getLineMetrics(aci,
                    lbm.getPosition(), offset, fontRenderContext);

                float lineHeight = lm.getAscent() + lm.getDescent()
                    + lm.getLeading();
                lineHeights += lineHeight;

                lbm.setPosition(offset);
            }

            preferredHeight = (int)Math.ceil(lineHeights);
        } else {
            LineMetrics lm = font.getLineMetrics(text, fontRenderContext);
            preferredHeight = (int)Math.ceil(lm.getAscent() + lm.getDescent()
                + lm.getLeading());
        }

        preferredHeight += (padding.top + padding.bottom);

        return preferredHeight;
    }

    @Override
    public Dimensions getPreferredSize() {
        int preferredWidth = 0;
        int preferredHeight = 0;

        Label label = (Label)getComponent();
        String text = label.getText();

        if (text != null
            && text.length() > 0) {
            Rectangle2D stringBounds = font.getStringBounds(text, fontRenderContext);
            preferredWidth = (int)Math.ceil(stringBounds.getWidth());
            preferredHeight = (int)Math.ceil(stringBounds.getHeight());
        } else {
            LineMetrics lm = font.getLineMetrics("", fontRenderContext);
            preferredHeight = (int)Math.ceil(lm.getAscent() + lm.getDescent()
                + lm.getLeading());
        }

        preferredWidth += (padding.left + padding.right);
        preferredHeight += (padding.top + padding.bottom);

        return new Dimensions(preferredWidth, preferredHeight);
    }

    
    @Override
    public int getBaseline(int width) {
      Label label = (Label)getComponent();
       
      /* calculate the baseline of the text */
      int baseline = -1;

      String text = label.getText();
      if (text == null) {
          text = "";
      }

      if (wrapText
          && text.length() > 0) {
          int contentWidth = label.getWidth() - (padding.left + padding.right);

          AttributedString attributedText = new AttributedString(text);
          attributedText.addAttribute(TextAttribute.FONT, font);

          AttributedCharacterIterator aci = attributedText.getIterator();
          LineBreakMeasurer lbm = new LineBreakMeasurer(aci, fontRenderContext);

          if (lbm.getPosition() < aci.getEndIndex()) {
              LineMetrics lm = font.getLineMetrics(text, fontRenderContext);
              baseline = (int)Math.ceil(lm.getAscent()-2);
          } else {
              // for multi-line labels, treat the baseline as being the baseline of the first line of text
              int offset = lbm.nextOffset(contentWidth);

              LineMetrics lm = font.getLineMetrics(aci,
                  lbm.getPosition(), offset, fontRenderContext);

              baseline = (int) Math.ceil(lm.getAscent()-2);
          }
      } else {
          LineMetrics lm = font.getLineMetrics(text, fontRenderContext);
          baseline = (int)Math.ceil(lm.getAscent()-2);
      }

      if (baseline!=-1) {
       baseline += padding.top;
      }

      return baseline;
    }
    
    @Override
    public void layout() {
        // No-op
    }

    @Override
    public void paint(Graphics2D graphics) {
        int width = getWidth();
        int height = getHeight();

        if (backgroundColor != null) {
            graphics.setPaint(backgroundColor);
            graphics.fillRect(0, 0, width, height);
        }

        Label label = (Label)getComponent();
        String text = label.getText();

        if (debugBaseline) {
            drawBaselineDebug(graphics);
        }
        
        if (text != null
            && text.length() > 0) {
            if (fontRenderContext.isAntiAliased()) {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    Platform.getTextAntialiasingHint());
            }

            if (fontRenderContext.usesFractionalMetrics()) {
                graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            }

            graphics.setFont(font);
            graphics.setPaint(color);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            float y = 0;
            switch (verticalAlignment) {
                case TOP: {
                    y = padding.top;
                    break;
                }

                case BOTTOM: {
                    y = height - getPreferredHeight(wrapText ? width : -1) + padding.top;
                    break;
                }

                case CENTER: {
                    y = (height - getPreferredHeight(wrapText ? width : -1)) / 2 + padding.top;
                    break;
                }
            }

            if (wrapText) {
                AttributedString attributedText = new AttributedString(text);
                attributedText.addAttribute(TextAttribute.FONT, font);

                AttributedCharacterIterator aci = attributedText.getIterator();
                LineBreakMeasurer lbm = new LineBreakMeasurer(aci, fontRenderContext);

                int contentWidth = width - (padding.left + padding.right);

                while (lbm.getPosition() < aci.getEndIndex()) {
                    TextLayout textLayout = lbm.nextLayout(contentWidth);
                    y += textLayout.getAscent();
                    drawText(graphics, textLayout, y);
                    y += textLayout.getDescent() + textLayout.getLeading();
                }
            } else {
                TextLayout textLayout = new TextLayout(text, font, fontRenderContext);
                drawText(graphics, textLayout, y + textLayout.getAscent());
            }
        }
    }

    private void drawText(Graphics2D graphics, TextLayout textLayout, float y) {
        float width = getWidth();
        Rectangle2D textBounds = textLayout.getBounds();

        float x = 0;
        switch (horizontalAlignment) {
            case LEFT: {
                x = padding.left;
                break;
            }

            case RIGHT: {
                x = width - (float)(textBounds.getX() + textBounds.getWidth()) -
                    padding.right;
                break;
            }

            case CENTER: {
                x = (width - (padding.left + padding.right) -
                    (float)(textBounds.getX() + textBounds.getWidth())) / 2f +
                    padding.left;
                break;
            }
        }

        textLayout.draw(graphics, x, y);

        if (textDecoration != null) {
            graphics.setStroke(new BasicStroke());

            float offset = 0;

            switch (textDecoration) {
                case UNDERLINE: {
                    offset = y + 2;
                    break;
                }

                case STRIKETHROUGH: {
                    offset = y - textLayout.getAscent() / 3f + 1;
                    break;
                }
            }

            Line2D line = new Line2D.Float(x, offset, x + (float)textBounds.getWidth(), offset);
            graphics.draw(line);
        }
    }

    /**
     * @return
     * <tt>false</tt>; labels are not focusable.
     */
    @Override
    public boolean isFocusable() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return (backgroundColor != null
            && backgroundColor.getTransparency() == Transparency.OPAQUE);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("font is null.");
        }

        this.font = font;
        invalidateComponent();
    }

    public final void setFont(String font) {
        if (font == null) {
            throw new IllegalArgumentException("font is null.");
        }

        setFont(decodeFont(font));
    }

    public final void setFont(Dictionary<String, ?> font) {
        if (font == null) {
            throw new IllegalArgumentException("font is null.");
        }

        setFont(Theme.deriveFont(font));
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color is null.");
        }

        this.color = color;
        repaintComponent();
    }

    public final void setColor(String color) {
        if (color == null) {
            throw new IllegalArgumentException("color is null.");
        }

        setColor(GraphicsUtilities.decodeColor(color));
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaintComponent();
    }

    public final void setBackgroundColor(String backgroundColor) {
        if (backgroundColor == null) {
            throw new IllegalArgumentException("backgroundColor is null");
        }

        setBackgroundColor(GraphicsUtilities.decodeColor(backgroundColor));
    }

    public TextDecoration getTextDecoration() {
        return textDecoration;
    }

    public void setTextDecoration(TextDecoration textDecoration) {
        this.textDecoration = textDecoration;
        invalidateComponent();
    }

    public final void setTextDecoration(String textDecoration) {
        if (textDecoration == null) {
            throw new IllegalArgumentException("textDecoration is null.");
        }

        setTextDecoration(TextDecoration.valueOf(textDecoration.toUpperCase()));
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        if (horizontalAlignment == null) {
            throw new IllegalArgumentException("horizontalAlignment is null.");
        }

        this.horizontalAlignment = horizontalAlignment;
        repaintComponent();
    }

    public final void setHorizontalAlignment(String horizontalAlignment) {
        if (horizontalAlignment == null) {
            throw new IllegalArgumentException("horizontalAlignment is null.");
        }

        setHorizontalAlignment(HorizontalAlignment.valueOf(horizontalAlignment.toUpperCase()));
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        if (verticalAlignment == null) {
            throw new IllegalArgumentException("verticalAlignment is null.");
        }

        this.verticalAlignment = verticalAlignment;
        repaintComponent();
    }

    public final void setVerticalAlignment(String verticalAlignment) {
        if (verticalAlignment == null) {
            throw new IllegalArgumentException("verticalAlignment is null.");
        }

        setVerticalAlignment(VerticalAlignment.valueOf(verticalAlignment.toUpperCase()));
    }

    public Insets getPadding() {
        return padding;
    }

    public void setPadding(Insets padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        this.padding = padding;
        invalidateComponent();
    }

    public final void setPadding(Dictionary<String, ?> padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        setPadding(new Insets(padding));
    }

    public final void setPadding(int padding) {
        setPadding(new Insets(padding));
    }

    public final void setPadding(Number padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        setPadding(padding.intValue());
    }

    public final void setPadding(String padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        setPadding(Insets.decode(padding));
    }

    public boolean getWrapText() {
        return wrapText;
    }

    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
        invalidateComponent();
    }

    // Label events
    @Override
    public void textChanged(Label label, String previousText) {
        invalidateComponent();
    }

    @Override
    public void textKeyChanged(Label label, String previousTextKey) {
        // No-op
    }
}
