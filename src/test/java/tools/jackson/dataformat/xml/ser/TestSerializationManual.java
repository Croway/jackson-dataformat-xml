package tools.jackson.dataformat.xml.ser;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.XmlTestBase;

public class TestSerializationManual extends XmlTestBase
{
    public static class Value {
        public int num;

        public Value(int n) { num = n; }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    public void testIssue54() throws Exception
    {
        XmlMapper xmlMapper = XmlMapper.builder()
                .enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .build();
        StringWriter sw = new StringWriter();
        ToXmlGenerator generator = (ToXmlGenerator) xmlMapper.createGenerator(sw);
        generator.initGenerator();

        generator.setNextName(new QName("items"));
        generator.writeStartObject();
        ArrayList<Value> values = new ArrayList<Value>();
        values.add(new Value(13));
        values.add(new Value(456));
        for (Value value : values) {
            generator.writeName("foo");
            generator.setNextName(new QName("item"));
            generator.writePOJO(value);
        }
        generator.writeEndObject();
        generator.close();
        
        String xml = sw.toString();
        
        // Remove XML declaration
        assertTrue(xml.startsWith("<?xml version"));
        int ix = xml.indexOf("?>");
        xml = xml.substring(ix+2).trim();
        
        assertEquals("<items><item><num>13</num></item><item><num>456</num></item></items>", xml);
   }
}