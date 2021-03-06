package cn.edu.jxufe.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DAO公共基类，由MybatisGenerator自动生成请勿修改
 * @param <Model> The Model Class 这里是泛型不是Model类
 * @param <PK> The Primary Key Class 如果是无主键，则可以用Model来跳过，如果是多主键则是Key类
 */
public interface MyBatisBaseDao<Model, PK extends Serializable> {
    int insert(Model record);
    List<Model> findAll();
    List<Model> findByParams(Model model);
    int update(Model record);
    Model selectByPrimaryKey(int pk);
    int delete(Model record);
    int insertSelective(Model record);
}