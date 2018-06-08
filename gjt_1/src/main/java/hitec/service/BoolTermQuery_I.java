package hitec.service;

import java.util.List;
import java.util.Map;

/**
 * @Description: bool Term 查询类
 * @author: fukl
 * @data: 2017年09月21日 17:02
 */
public interface BoolTermQuery_I {

    /**
     * 最新查语法--比较全
     * @param indices   //不可为空
     * @param types     //可以为空
     * @param params    //可以为空
     *       {
     *          "_id":"true",               //是否返回id，默认false
     *          "_type":"true",             //是否返回type，默认false
     *          "_index":"true",            //是否返回index，默认false
     *          "from":"10",                //从第几条开始返回数据， 默认0
     *          "size":"30",                //返回多少条数据，默认30
     *          "sortType":"desc",          //排序类型，默认desc倒序
     *          "sort":"last_time",         //可以为空
     *          "resultAll":"false",        //是否返回所有数据 , 默认false
     *          "must":{                    //可以为空,term查询，类似 ==
     *              "key_name":"value",
     *                  .....
     *          },
     *          "mustNot":{                 //可以为空，term查询，类似 !=
     *              "key_name":"value",
     *                  .....
     *          },
     *          "range":[                   //可以为空，数值比较查询
     *              {
     *                  "name":"value",
     *                  "gt":"10",
     *                  "lte":"30"
     *              },
     *              ....
     *          ]
     *       }
     * @return
     * @throws Exception
     */
    public List<Map> query_new(String[] indices, String[] types , Map<String,Object> params) throws Exception;

    public List<Map> query(String[] indices, String[] types , Map<String,Object> params) throws Exception;

    public Map query_resultId(String[] indices, String[] types, Map<String, Object> params) throws Exception;
}
