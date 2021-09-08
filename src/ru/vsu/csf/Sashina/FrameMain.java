package ru.vsu.csf.Sashina;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class FrameMain extends JFrame{
    private JPanel panelMain;
    private JTextArea textGraph;
    private JPanel graphPaint;
    private JButton fromFile;
    private JTextField fromFileField;
    private JButton toFile;
    private JTextField toFileField;
    private JComboBox comboBoxGraph;
    private JButton drawGraph;
    private JButton cycleButton;
    private JLabel label;
    private JButton clear;

    private Graph Graph = null;
    private boolean cycle = false;

    private SvgPanel panelGraphPainter;

    private static class SvgPanel extends JPanel {
        private String svg = null;
        private GraphicsNode svgGraphicsNode = null;

        public void paint(String svg) throws IOException {
            String xmlParser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory df = new SAXSVGDocumentFactory(xmlParser);
            SVGDocument doc = df.createSVGDocument(null, new StringReader(svg));
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            BridgeContext ctx = new BridgeContext(userAgent, loader);
            ctx.setDynamicState(BridgeContext.DYNAMIC);
            GVTBuilder builder = new GVTBuilder();
            svgGraphicsNode = builder.build(ctx, doc);

            this.svg = svg;
            repaint();
        }

        @Override
        public void paintComponent(Graphics gr) {
            super.paintComponent(gr);

            if (svgGraphicsNode == null) {
                return;
            }

            double scaleX = this.getWidth() / svgGraphicsNode.getPrimitiveBounds().getWidth();
            double scaleY = this.getHeight() / svgGraphicsNode.getPrimitiveBounds().getHeight();
            double scale = Math.min(scaleX, scaleY);
            AffineTransform transform = new AffineTransform(scale, 0, 0, scale, 0, 0);
            svgGraphicsNode.setTransform(transform);
            Graphics2D g2d = (Graphics2D) gr;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            svgGraphicsNode.paint(g2d);
        }
    }

    private static String dotToSvg(String dotSrc) throws IOException {
        MutableGraph g = new Parser().read(dotSrc);
        return Graphviz.fromGraph(g).render(Format.SVG).toString();
    }

    public FrameMain () {
        this.setTitle("Графы");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        graphPaint.setLayout(new BorderLayout());
        panelGraphPainter = new SvgPanel();
        graphPaint.add(new JScrollPane(panelGraphPainter));

        fromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String file = fromFileField.getText();
                    if (!file.contains(".txt"))
                        file += ".txt";
                    List<String> list = Files.readFile(file);
                    for (String s : list) {
                        textGraph.append(s);
                        textGraph.append("\n");
                    }
                } catch (Exception exp) {
                    label.setText("Такого файла не существует");
                }
            }
        });

        toFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String file = toFileField.getText();
                    if (!file.contains(".txt"))
                        file += ".txt";
                    String s;
                    if (cycle)
                        s = label.getText();
                    else
                        s = "Цикла нет";
                    Files.writeToFile(textGraph, s, file);
                } catch (Exception exp) {
                    label.setText("Ошибка в сохранении");
                }
            }
        });

        drawGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = comboBoxGraph.getSelectedItem().toString();
                    Matcher matcher = Pattern.compile(".*\\W(\\w+)\\s*\\)\\s*$").matcher(name);
                    matcher.find();
                    String className = matcher.group(1);
                    Class clz = Class.forName("ru.vsu.csf.Sashina." + className);
                    Graph graph = GraphUtils.fromStr(textGraph.getText(), clz);
                    FrameMain.this.Graph = graph;
                    panelGraphPainter.paint(dotToSvg(GraphUtils.toDot(graph)));
                } catch (Exception exp) {
                    label.setText("Не удается нарисовать граф");
                }
            }
        });

        cycleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cycle = Graph.existenceOfCycle();
                    if (cycle)
                        label.setText("Цикл существует: " + Graph.findCycle());
                    else
                        label.setText("Цикла нет");
                } catch (Exception exp) {
                    label.setText("Произошла ошибка");
                }
            }
        });

        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textGraph.setText("");
                fromFileField.setText("");
                toFileField.setText("");
                label.setText("");
                Graph = null;
                cycle = false;
            }
        });
    }
}
