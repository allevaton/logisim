/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.std.arith;

import com.cburch.logisim.data.*;
import com.cburch.logisim.instance.*;
import com.cburch.logisim.tools.key.BitWidthConfigurator;
import com.cburch.logisim.util.GraphicsUtil;

import java.awt.*;

import static com.cburch.logisim.util.LocaleString.getFromLocale;

public class Subtractor extends InstanceFactory {
    private static final int IN0 = 0;
    private static final int IN1 = 1;
    private static final int OUT = 2;
    private static final int B_IN = 3;
    private static final int B_OUT = 4;

    public Subtractor() {
        super("Subtractor", getFromLocale("subtractorComponent"));
        setAttributes(new Attribute[]{StdAttr.WIDTH},
                new Object[]{BitWidth.create(8)});
        setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
        setOffsetBounds(Bounds.create(-40, -20, 40, 40));
        setIconName("subtractor.svg");

        Port[] ps = new Port[5];
        ps[IN0] = new Port(-40, -10, Port.INPUT, StdAttr.WIDTH);
        ps[IN1] = new Port(-40, 10, Port.INPUT, StdAttr.WIDTH);
        ps[OUT] = new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH);
        ps[B_IN] = new Port(-20, -20, Port.INPUT, 1);
        ps[B_OUT] = new Port(-20, 20, Port.OUTPUT, 1);
        ps[IN0].setToolTip(getFromLocale("subtractorMinuendTip"));
        ps[IN1].setToolTip(getFromLocale("subtractorSubtrahendTip"));
        ps[OUT].setToolTip(getFromLocale("subtractorOutputTip"));
        ps[B_IN].setToolTip(getFromLocale("subtractorBorrowInTip"));
        ps[B_OUT].setToolTip(getFromLocale("subtractorBorrowOutTip"));
        setPorts(ps);
    }

    @Override
    public void propagate(InstanceState state) {
        // get attributes
        BitWidth data = state.getAttributeValue(StdAttr.WIDTH);

        // compute outputs
        Value a = state.getPort(IN0);
        Value b = state.getPort(IN1);
        Value b_in = state.getPort(B_IN);
        if (b_in == Value.UNKNOWN || b_in == Value.NIL) {
            b_in = Value.FALSE;
        }

        Value[] outs = Adder.computeSum(data, a, b.not(), b_in.not());

        // propagate them
        int delay = (data.getWidth() + 4) * Adder.PER_DELAY;
        state.setPort(OUT, outs[0], delay);
        state.setPort(B_OUT, outs[1].not(), delay);
    }

    @Override
    public void paintInstance(InstancePainter painter) {
        Graphics g = painter.getGraphics();
        painter.drawBounds();

        g.setColor(Color.GRAY);
        painter.drawPort(IN0);
        painter.drawPort(IN1);
        painter.drawPort(OUT);
        painter.drawPort(B_IN, "b in", Direction.NORTH);
        painter.drawPort(B_OUT, "b out", Direction.SOUTH);

        Location loc = painter.getLocation();
        int x = loc.getX();
        int y = loc.getY();
        GraphicsUtil.switchToWidth(g, 2);
        g.setColor(Color.BLACK);
        g.drawLine(x - 15, y, x - 5, y);
        GraphicsUtil.switchToWidth(g, 1);
    }
}
