package com.movie2night.di

import android.content.Context
import com.movie2night.data.local.datastore.AuthDataStore
import com.movie2night.data.remote.api.AuthApi
import com.movie2night.data.remote.api.ChatApi
import com.movie2night.data.remote.api.MatchApi
import com.movie2night.data.remote.api.MovieApi
import com.movie2night.data.remote.api.UserApi
import com.movie2night.data.repository.AuthRepositoryImpl
import com.movie2night.data.repository.ChatRepositoryImpl
import com.movie2night.data.repository.MatchRepositoryImpl
import com.movie2night.data.repository.MovieRepositoryImpl
import com.movie2night.data.repository.UserRepositoryImpl
import com.movie2night.domain.repository.AuthRepository
import com.movie2night.domain.repository.ChatRepository
import com.movie2night.domain.repository.MatchRepository
import com.movie2night.domain.repository.MovieRepository
import com.movie2night.domain.repository.UserRepository
import com.movie2night.domain.usecase.GetMoviesUseCase
import com.movie2night.domain.usecase.LoginUseCase
import com.movie2night.domain.usecase.RateUserUseCase
import com.movie2night.domain.usecase.RegisterUseCase
import com.movie2night.domain.usecase.SendMatchRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideAuthDataStore(@ApplicationContext context: Context): AuthDataStore =
        AuthDataStore(context)

    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides @Singleton
    fun provideMovieApi(retrofit: Retrofit): MovieApi = retrofit.create(MovieApi::class.java)

    @Provides @Singleton
    fun provideMatchApi(retrofit: Retrofit): MatchApi = retrofit.create(MatchApi::class.java)

    @Provides @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi = retrofit.create(ChatApi::class.java)

    @Provides @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        authDataStore: AuthDataStore
    ): AuthRepository = AuthRepositoryImpl(authApi, authDataStore)

    @Provides @Singleton
    fun provideMovieRepository(movieApi: MovieApi): MovieRepository =
        MovieRepositoryImpl(movieApi)

    @Provides @Singleton
    fun provideMatchRepository(matchApi: MatchApi): MatchRepository =
        MatchRepositoryImpl(matchApi)

    @Provides @Singleton
    fun provideChatRepository(chatApi: ChatApi): ChatRepository =
        ChatRepositoryImpl(chatApi)

    @Provides @Singleton
    fun provideUserRepository(userApi: UserApi): UserRepository =
        UserRepositoryImpl(userApi)

    @Provides @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase =
        LoginUseCase(authRepository)

    @Provides @Singleton
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase =
        RegisterUseCase(authRepository)

    @Provides @Singleton
    fun provideGetMoviesUseCase(movieRepository: MovieRepository): GetMoviesUseCase =
        GetMoviesUseCase(movieRepository)

    @Provides @Singleton
    fun provideSendMatchRequestUseCase(matchRepository: MatchRepository): SendMatchRequestUseCase =
        SendMatchRequestUseCase(matchRepository)

    @Provides @Singleton
    fun provideRateUserUseCase(userRepository: UserRepository): RateUserUseCase =
        RateUserUseCase(userRepository)
}