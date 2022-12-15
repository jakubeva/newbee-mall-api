package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.MyIKAnalyzer;
import ltd.newbee.mall.dao.GoodsCategoryMapper;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.SearchService;
import ltd.newbee.mall.util.LuceneUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    @Override
    public List<NewBeeMallGoods> getNewBeeMallGoodsByPage(PageQueryUtil pageUtil) {
        //根据分页查询参数获取商品列表
        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsList(pageUtil);
        //获取商品列表总数
        int total = goodsMapper.getTotalNewBeeMallGoods(pageUtil);
        //封装分页结果并返回
        return goodsList;
    }
    @Override
    public PageResult getNewBeeMallGoodsPage(PageQueryUtil pageUtil) {
        //根据分页查询参数获取商品列表
        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsList(pageUtil);
        //获取商品列表总数
        int total = goodsMapper.getTotalNewBeeMallGoods(pageUtil);
        //封装分页结果并返回
        return new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
    }


    @Override
    public void searchDocumentByIndex(String keyword) {
        try {
// 2、 执行搜索
// a) 指定索引库目录
            //创建搜索属性区间
            String[] str = {"goods_name", "goods_intro"};
            LuceneUtil luceneUtil = new LuceneUtil();
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("static-files\\Lucene\\ind"));
            // 索引读取工具
            IndexReader reader = DirectoryReader.open(directory);
            // 索引搜索工具
            IndexSearcher searcher = new IndexSearcher(reader);
            // 创建查询解析器,两个参数：默认要查询的字段的名称，分词器
            // 创建查询解析器,两个参数：默认要查询的字段的名称，分词器
            MultiFieldQueryParser parser = new MultiFieldQueryParser(str, new MyIKAnalyzer());
// d) 通过IndexSearcher对象执行查询索引库，返回TopDocs对象
// 第一个参数：查询对象
            Query query = parser.parse(keyword);
// 第二个参数：最大的n条记录
// 获取前n条记录
            TopDocs topDocs = searcher.search(query, 10);
            // 获取总条数
            System.out.println("本次搜索共找到" + topDocs.totalHits + "条数据");
            // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 取出文档编号
                int docID = scoreDoc.doc;
                // 根据编号去找文档
                Document doc = reader.document(docID);
// f) 输出文档内容
                System.out.println("===============================");
                System.out.println("文档id:" + docID);
                System.out.println("商品id:" + doc.get("goods_id"));
                System.out.println("商品name:" + doc.get("goods_name"));
                System.out.println("商品简介:" + doc.get("goods_intro"));
                System.out.println("商品图片:" + doc.get("goods_cover_img"));
                System.out.println("商品价格:" + doc.get("goods_sell_status"));
            }
// g) 关闭IndexReader
            reader.close();
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createIndex() throws ClassNotFoundException, SQLException, IOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<NewBeeMallGoods> list = new ArrayList<>();
        Class.forName("com.mysql.jdbc.Driver");

// 连接数据库

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/newbee_mall_db_v2", "root", "123456");

// SQL语句

        String sql = "SELECT * FROM tb_newbee_mall_goods_info";

// 创建preparedStatement

        preparedStatement = connection.prepareStatement(sql);
        // 获取结果集

        resultSet = preparedStatement.executeQuery();

// 结果集解析

        while (resultSet.next()) {

            NewBeeMallGoods goods = new NewBeeMallGoods();

            goods.setGoodsId(resultSet.getLong("goods_id"));
            goods.setGoodsName(resultSet.getString("goods_name"));
            goods.setGoodsIntro(resultSet.getString("goods_intro"));
            goods.setGoodsCategoryId(resultSet.getLong("goods_category_id"));
            goods.setGoodsCoverImg(resultSet.getString("goods_cover_img"));
            goods.setGoodsCarousel(resultSet.getString("goods_carousel"));
            goods.setGoodsDetailContent(resultSet.getString("goods_detail_content"));
            goods.setOriginalPrice(resultSet.getInt("original_price"));
            goods.setSellingPrice(resultSet.getInt("selling_price"));
            goods.setStockNum(resultSet.getInt("stock_num"));
            goods.setTag(resultSet.getString("tag"));
            goods.setGoodsSellStatus(resultSet.getByte("goods_sell_status"));
            goods.setCreateUser(resultSet.getInt("create_user"));
            goods.setCreateTime(resultSet.getTimestamp("create_time"));
            goods.setUpdateUser(resultSet.getInt("update_user"));
            goods.setUpdateTime(resultSet.getTimestamp("update_time"));


            list.add(goods);

        }

        Collection<Document> docList = new ArrayList<>();


        for (int i = 0; i < list.size(); i++) {
            //contentMapper.insertSelective(list1.get(i));
            // 创建文档对象
            Document doc = new Document();
            //StringField会创建索引，但是不会被分词，TextField，即创建索引又会被分词。
            TextField goodsName = new TextField("goods_name", list.get(i).getGoodsName(), Field.Store.YES);
            TextField goodsIntro = new TextField("goods_intro", list.get(i).getGoodsIntro(), Field.Store.YES);
            TextField goodsCategoryId = new TextField("goods_category_id", list.get(i).getGoodsCategoryId().toString(), Field.Store.YES);
            TextField goodsCoverImg = new TextField("goods_cover_img", list.get(i).getGoodsCoverImg(), Field.Store.YES);
            TextField goodsCarousel = new TextField("goods_carousel", list.get(i).getGoodsCarousel(), Field.Store.YES);
            TextField goodsDetailContent = new TextField("goods_detail_content", list.get(i).getGoodsDetailContent(), Field.Store.YES);
            TextField originalPrice = new TextField("original_price", list.get(i).getOriginalPrice().toString(), Field.Store.YES);
            TextField sellingPrice = new TextField("selling_price", list.get(i).getSellingPrice().toString(), Field.Store.YES);
            TextField stockNum = new TextField("stock_num", list.get(i).getStockNum().toString(), Field.Store.YES);
            TextField tag = new TextField("tag", list.get(i).getTag(), Field.Store.YES);
            TextField goodsSellStatus = new TextField("goods_sell_status", list.get(i).getGoodsSellStatus().toString(), Field.Store.YES);
            TextField createUser = new TextField("create_user", list.get(i).getCreateUser().toString(), Field.Store.YES);
            TextField createTime = new TextField("create_time", list.get(i).getCreateTime().toString(), Field.Store.YES);
            TextField updateUser = new TextField("update_user", list.get(i).getUpdateUser().toString(), Field.Store.YES);
            TextField updateTime = new TextField("update_time", list.get(i).getUpdateTime().toString(), Field.Store.YES);

// 把域(Field)添加到文档(Document)中

            doc.add(new StringField("goods_id", list.get(i).getGoodsId().toString(), Field.Store.YES));
            doc.add(goodsName);
            doc.add(goodsIntro);
            doc.add(goodsCategoryId);
            doc.add(goodsCoverImg);
            doc.add(goodsCarousel);
            doc.add(goodsDetailContent);
            doc.add(originalPrice);
            doc.add(sellingPrice);
            doc.add(stockNum);
            doc.add(tag);
            doc.add(goodsSellStatus);
            doc.add(createUser);
            doc.add(createTime);
            doc.add(updateUser);
            doc.add(updateTime);

            docList.add(doc);

            docList.add(doc);
        }

        Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("E:\\Lucene\\ind"));
        // 引入IK分词器
        Analyzer analyzer = new MyIKAnalyzer();
        // 索引写出工具的配置对象，这个地方就是最上面报错的问题解决方案
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        // 设置打开方式：OpenMode.APPEND 会在索引库的基础上追加新索引。OpenMode.CREATE会先清空原来数据，再提交新的索引
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        // 创建索引的写出工具类。参数：索引的目录和配置信息
        IndexWriter indexWriter = new IndexWriter(directory, conf);
        // 把文档集合交给IndexWriter
        indexWriter.addDocuments(docList);
        // 提交
        indexWriter.commit();
        // 关闭
        indexWriter.close();
        System.out.println("ssssssssssss");
    }

    public List<NewBeeMallGoods> search(String keyword) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
        Directory directory1=FSDirectory.open(FileSystems.getDefault().getPath("E:\\Lucene\\ind"));
        // 索引读取工具
        IndexReader reader= DirectoryReader.open(directory1);
        // 索引搜索工具
        IndexSearcher searcher=new IndexSearcher(reader);
        // 创建查询解析器,两个参数：默认要查询的字段的名称，分词器

        String[] fields = {"goods_name", "goods_intro"};
        MultiFieldQueryParser parser= new MultiFieldQueryParser(fields,new MyIKAnalyzer());
        // 创建查询对象
        Query query=parser.parse(keyword);

        // 获取前n条记录
        TopDocs topDocs=searcher.search(query,50);

        // 获取总条数
        System.out.println("本次搜索共找到"+topDocs.totalHits+"条数据");
        // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
        ScoreDoc[]scoreDocs=topDocs.scoreDocs;

        List<NewBeeMallGoods>list1=new ArrayList<>();
        for(ScoreDoc scoreDoc:scoreDocs){
            // 取出文档编号
            int docID=scoreDoc.doc;
            // 根据编号去找文档
            Document doc=reader.document(docID);
            //ltd.newbee.mall.api.random.Content content = contentMapper.selectByPrimaryKey(doc.get("id"));
            NewBeeMallGoods good=new NewBeeMallGoods();

            good.setGoodsId(Long.parseLong(doc.get("goods_id")));
            good.setGoodsName(doc.get("goods_name"));
            good.setGoodsIntro(doc.get("goods_intro"));
            good.setGoodsCarousel(doc.get("goods_category_id"));
            good.setGoodsCoverImg(doc.get("goods_cover_img"));
            good.setGoodsCarousel(doc.get("goods_carousel"));
            good.setGoodsDetailContent(doc.get("goods_detail_content"));
            good.setOriginalPrice(Integer.valueOf(doc.get("original_price")));
            good.setSellingPrice(Integer.valueOf(doc.get("selling_price")));
            good.setStockNum(Integer.parseInt(doc.get("stock_num")));
            good.setTag(doc.get("tag"));
            good.setGoodsSellStatus(Byte.parseByte(doc.get("goods_sell_status")));
            good.setCreateUser(Integer.parseInt(doc.get("create_user")));
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String time = doc.get("create_time");
            java.util.Date date = ft.parse(time);
            good.setCreateTime(date);
            good.setUpdateUser(Integer.parseInt(doc.get("update_user")));
            String time1 = doc.get("update_time");
            Date date1 = ft.parse(time1);
            good.setUpdateTime(date1);




            list1.add(good);
        }
        return list1;
    }



}

