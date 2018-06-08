package hitec.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * 
 * @ClassName: DataInfo 
 * @Description: TODO(连接数据库，查询告警台左侧告警分类的实体类) 
 * @author HYW
 * @date 2018年3月12日 下午2:21:45 
 *
 */
@Entity
public class DataInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -5898458914568801789L;

    @Id
    @GeneratedValue
    private int pkId;
    
    private int id;
    private int parentId;
    private String name;
    private int isData;
    private String subName;
    
    public DataInfo() {}

    public DataInfo(int pkId, int id, int parentId, String name, int isData, String subName) {
        super();
        this.pkId = pkId;
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.isData = isData;
        this.subName = subName;
    }

    public int getPkId() {
        return pkId;
    }

    public void setPkId(int pkId) {
        this.pkId = pkId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsData() {
        return isData;
    }

    public void setIsData(int isData) {
        this.isData = isData;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }
    
}
