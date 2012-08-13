package net.anzix.tool;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class DrupalSecRss {

    @Option(name = "-d", usage = "Url of the drupal portal", required = true)
    private String url;

    @Option(name = "-u", usage = "Drupal user name", required = true)
    private String username;

    @Option(name = "-p", usage = "Drupal password", required = true)
    private String password;

    @Option(name = "-o", usage = "Rss output file", required = false)
    private String outputFile = "rss.xml";

    public static void main(String[] args) {

        DrupalSecRss main = new DrupalSecRss();
        CmdLineParser parser = new CmdLineParser(main);
        try {
            parser.parseArgument(args);
            main.start();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err
                    .println("java -jar DrupalSecRss.jar [options...]");
            parser.printUsage(System.err);
            return;
        }

    }

    private void start() {
        try {
            final WebClient webClient = new WebClient();
            webClient.setUseInsecureSSL(true);
            final HtmlPage page = webClient.getPage(url + "/user/login");

            HtmlForm form = page.getForms().get(0);
            form.getInputByName("name").setValueAttribute(username);
            form.getInputByName("pass").setValueAttribute(password);
            HtmlPage page2 = form.getInputByName("op").click();

            XmlPage rssPage = webClient.getPage(url + "/rss.xml");
            if (outputFile != null) {
                try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {
                    writer.write(rssPage.getContent());
                    writer.close();
                }
            } else {
                System.out.println(rssPage.getContent());
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
