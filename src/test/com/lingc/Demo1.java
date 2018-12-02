package com.lingc;

import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * @author wzp
 * @description
 * @time Create at 2018/12/2 23:12
 * @modified
 */
public class Demo1 {

    private static final File DB_PATH = new File("F:\\JAVA\\neo4j-community-3.5.0\\data\\databases\\graph.db");


    /**
     * 服务器开发的代码
     */
    @Test
    public void test1() {
        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "123456" ) );
        Session session = driver.session();
        session.run( "CREATE (a:Person {name: {name}, title: {title}})",
                parameters( "name", "Arthur001", "title", "King001" ) );

        StatementResult result = session.run( "MATCH (a:Person) WHERE a.name = {name} " +
                        "RETURN a.name AS name, a.title AS title",
                parameters( "name", "Arthur001" ) );
        while ( result.hasNext() )
        {
            Record record = result.next();
            System.out.println( record.get( "title" ).asString() + " " + record.get( "name" ).asString() );
        }
        session.close();
        driver.close();

    }

    /**
     * 嵌入式开发
     */
    @Test
    public void test2() {
        // TODO Auto-generated method stub
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File("Data/Test"));
        System.out.println("Database Load!");
        //开启事务
        try (Transaction tx = graphDb.beginTx()) {
            // Perform DB operations
            Node steve = graphDb.createNode(Labels.USER);
            steve.setProperty("name", "Steve");
            Node linda = graphDb.createNode(Labels.USER);
            linda.setProperty("name", "Linda");
            steve.createRelationshipTo( linda, RelationshipTypes.IS_FRIEND_OF );
            System.out.println("created node name is" + steve.getProperty("name"));
            tx.success();
        }
        //查询数据库
        String query ="match (n:USER) return n.name as name";
        Map<String, Object >parameters = new HashMap<String, Object>();
        try ( Result result = graphDb.execute( query, parameters ) )
        {
            while ( result.hasNext() )
            {
                Map<String, Object> row = result.next();
                for ( String key : result.columns() )
                {
                    System.out.printf( "%s = %s%n", key, row.get( key ) );
                }
            }
        }

        registerShutdownHook(graphDb);
        System.out.println("Database Shutdown!");

    }
    //设置标签，但是必须继承于接口label
    public enum Labels implements Label {
        USER,
        MOVIE;
    }

    public enum RelationshipTypes implements RelationshipType {
        IS_FRIEND_OF,
        HAS_SEEN;
    }


    private static void registerShutdownHook(final GraphDatabaseService graphDb){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                graphDb.shutdown();
            }
        });
    }

}
