package com.wooshe.VKContests.Realm;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class Util
{
    public static void AllRemove(Realm realm, RealmResults<RealmObject> obj)
    {
        realm.beginTransaction();

        if(!obj.isEmpty())
        {
            for(int i = obj.size() - 1; i >= 0; i--)
            {
                obj.get(i).removeFromRealm();
            }
        }
        realm.commitTransaction();
    }

    public static Realm OpenTable(RealmConfiguration realmConfiguration)
    {
        Realm realm;
        try
        {
            realm= Realm.getInstance(realmConfiguration);
        }
        catch (RealmMigrationNeededException e)
        {
            try
            {
                Realm.deleteRealm(realmConfiguration);
                realm =  Realm.getInstance(realmConfiguration);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        return realm;
    }
}
