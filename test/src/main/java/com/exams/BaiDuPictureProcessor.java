package com.exams;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.fc.file.DownImage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author fangcong on 2018/8/23.
 */
public class BaiDuPictureProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    private static final String THUMB_URL_PATTERN = "https://ss\\d\\.bdstatic\\.com/.*\\.jpg";

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex(
            "https://image\\.baidu\\.com/search/index\\?ct=201326592&z=&tn=baiduimage&ipn=r&word=%E5%A3%81%E7%BA%B8"
                + "%20%E4%B8%8D%E5%90%8C%E9%A3%8E%E6%A0%BC%20%E7%BE%8E%E5%A5%B3&pn=0.*").all());
        //page.putField("title", page.getHtml().$("img", "src").all());

        System.out.println(jsonProcess(page.getHtml().toString()));

        //page.putField("question", page.getHtml().xpath("//div[@class='QuestionRichText']//tidyText()").toString());
        //page.putField("answer", page.getHtml().xpath("//div[@class='QuestionAnswer-content']/tidyText()").toString());
        if (page.getResultItems().get("title") == null) {
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String baseUrl = "http://www.nipic.com/photo/renwu/mingxing/index.html";
        OOSpider.create(new BaiDuPictureProcessor()).addUrl(
            "https://image.baidu.com/search/index?ct=201326592&z=&tn=baiduimage&ipn=r&word=%E5%A3%81%E7%BA%B8%20%E4"
                + "%B8%8D%E5%90%8C%E9%A3%8E%E6%A0%BC%20%E7%BE%8E%E5%A5%B3&pn=0&istype=2&ie=utf-8&oe=utf-8&cl=2&lm=-1"
                + "&st=-1&fr=&fmq=1526269427171_R&ic=0&se=&sme=&width=&height=&face=0")
            .thread(4).run();
        //jsoupProcess(baseUrl);
    }

    public static void jsoupProcess(String url) {
        try {
            Document document = Jsoup.connect(url).post();
            Elements imgTags = document.select("img[src]");
            imgTags.forEach(element -> {
                String absSrc = element.attr("abs:src");
                System.out.println("abs-src=" + absSrc);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String jsonProcess(String html) {
        String[] strs = html.split("\"thumbURL\":");
        Pattern p = Pattern.compile(THUMB_URL_PATTERN);
        StringBuilder sb = new StringBuilder();

        Stream.of(strs).forEach(str -> {
            Matcher m = p.matcher(str);
            while (m.find()) {
                String picUrl = m.group().split("\"")[0];
                sb.append(picUrl).append(";");
                String filePath = "F://test/images";
                try {
                    DownImage.download(picUrl,
                        filePath + picUrl.substring(picUrl.lastIndexOf("/")) + ".jpg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return sb.toString();
    }
}
