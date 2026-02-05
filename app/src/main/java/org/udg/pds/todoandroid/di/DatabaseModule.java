package org.udg.pds.todoandroid.di;

import android.content.Context;

import androidx.room.Room;

import org.udg.pds.todoandroid.data.db.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public AppDatabase provideDb(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "demo-db")
                .fallbackToDestructiveMigration()
                .build();
    }
}
