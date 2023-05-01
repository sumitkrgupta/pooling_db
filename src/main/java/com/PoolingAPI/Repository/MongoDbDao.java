package com.PoolingAPI.Repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Repository
public interface MongoDbDao {

    /**
     * @param DBName : DB Name
     * @param ExistCollection : Exist collection name
     */
    @Transactional
    boolean collectionExist(final String DBName,final String ExistCollection);
    /**
     * @param DBName : DB Name
     * @param ExistCollection : Exist collection name
     * @param renameCollectionTo :rename collection to                       :
     */
    @Transactional
    boolean renameCollection(final String DBName,final String ExistCollection,final String renameCollectionTo);
    /**
     * @param DropRenameCollection : Drop collection
     */
    @Transactional
    boolean dropExistingCollection(final String DropRenameCollection);

    void writeLastInsertedId(int lastID,String filePath,String lastInsertedKey,String dateKey) throws IOException;
}
