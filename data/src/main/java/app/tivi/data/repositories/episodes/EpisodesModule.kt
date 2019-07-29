/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.tivi.data.repositories.episodes

import app.tivi.inject.Tmdb
import app.tivi.inject.Trakt
import dagger.Binds
import dagger.Module

@Module
abstract class EpisodesModule {
    @Binds
    @Trakt
    abstract fun bindTraktSeasonsEpisodesDataSource(source: TraktSeasonsEpisodesDataSource): SeasonsEpisodesDataSource

    @Binds
    @Trakt
    abstract fun bindTraktEpisodeDataSource(source: TraktEpisodeDataSource): EpisodeDataSource

    @Binds
    @Tmdb
    abstract fun bindTmdbEpisodeDataSource(source: TmdbEpisodeDataSource): EpisodeDataSource
}