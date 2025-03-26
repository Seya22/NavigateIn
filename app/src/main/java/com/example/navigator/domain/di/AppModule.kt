package com.example.navigator.domain.di

import android.app.Application
import androidx.room.Room
import com.example.navigator.domain.use_cases.GetDestinationDesc
import com.example.navigator.data.data_source.Database
import com.example.navigator.data.ml.classification.TextAnalyzer
import com.example.navigator.data.pathfinding.AStarImpl
import com.example.navigator.data.repository.GraphImpl
import com.example.navigator.data.repository.RecordsImpl
import com.example.navigator.domain.ml.ObjectDetector
import com.example.navigator.domain.pathfinding.Pathfinder
import com.example.navigator.domain.repository.GraphRepository
import com.example.navigator.domain.repository.RecordsRepository
import com.example.navigator.domain.tree.Tree
import com.example.navigator.domain.use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val DATABASE_NAME = "nodes"
    private const val DATABASE_DIR = "database/nodes.db"

    @Provides
    @Singleton
    fun provideDatabase(app: Application): Database {
        return Room.databaseBuilder(app, Database::class.java, DATABASE_NAME)
//            .createFromAsset(DATABASE_DIR)
            .allowMainThreadQueries()
            .addMigrations()
            .build()
    }

    @Provides
    @Singleton
    fun provideGraphRepository(database: Database): GraphRepository {
        return GraphImpl(database)
    }

    @Provides
    @Singleton
    fun provideRecordsRepository(database: Database): RecordsRepository {
        return RecordsImpl(database)
    }

    @Provides
    @Singleton
    fun provideTree(repository: GraphRepository): Tree {
        return Tree(repository)
    }

    @Provides
    @Singleton
    fun provideSmoothPath(): SmoothPath {
        return SmoothPath()
    }

    @Provides
    @Singleton
    fun providePathfinder(smoothPath: SmoothPath): Pathfinder {
        return AStarImpl(smoothPath)
    }

    @Provides
    @Singleton
    fun provideFindWay(pathfinder: Pathfinder): FindWay {
        return FindWay(pathfinder)
    }

    @Provides
    @Singleton
    fun provideObjectDetector(): ObjectDetector {
        return TextAnalyzer()
    }

    @Provides
    @Singleton
    fun provideImageAnalyzer(objectDetector: ObjectDetector): AnalyzeImage {
        return AnalyzeImage(objectDetector)
    }

    @Provides
    @Singleton
    fun provideHitTest(): HitTest {
        return HitTest()
    }

    @Provides
    @Singleton
    fun provideDestinationDest(): GetDestinationDesc {
        return GetDestinationDesc()
    }

}