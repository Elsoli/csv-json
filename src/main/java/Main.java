import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCsv = "data.csv";
        String fileNameXml = "data.xml";
        String fileNameJsonCsv = "data.json";
        String fileNameJsonXml = "data2.json";

        List<Employee> listByCsv = parseCSV(columnMapping, fileNameCsv);
        String jsonFromCsv = listToJson(listByCsv);
        writeString(jsonFromCsv, fileNameJsonCsv);

        List<Employee> listByXml = parseXML(fileNameXml);
        String jsonFromXml = listToJson(listByXml);
        writeString(jsonFromXml, fileNameJsonXml);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileNameCsv) {
        List<Employee> employeeList = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileNameCsv))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader).withMappingStrategy(strategy).build();
            employeeList = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public static List<Employee> parseXML(String xmlFileName) throws ParserConfigurationException, IOException, SAXException {
            List<Employee> employeeList = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(xmlFileName));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.ELEMENT_NODE == node.getNodeType()) {
                    Element employee = (Element) node;
                    long id = Long.parseLong(employee.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = employee.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = employee.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = employee.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent());
                    employeeList.add(new Employee(id, firstName, lastName, country, age));
                }
            }
            return employeeList;
    }

    public static String listToJson(List list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(list);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
