package com.matttske.gamelist.data

import java.io.Serializable

//GAME CLASS BEGIN
data class Game(
        var id: Int,
        var slug: String,
        var name: String,
        var released: String,
        var tba: Boolean,
        var background_image: String,
        var background_image_additional: String,
        var rating: String,
        var metacritic: String,
        var dominant_color: String,
        var genres: Array<Genre>,
        var esrb_rating: EsrbRating,
        var clip: Clip
        //var added_by_status: AddedByStatus
): Serializable

data class Genre(
        var id: Int,
        var name: String
): Serializable

data class EsrbRating(
        var id: Int,
        var name: String,
        var slug: String
): Serializable

data class AddedByStatus(
        var yet: Int,
        var owned: Int,
        var beaten: Int,
        var toplay: Int,
        var dropped: Int,
        var playing: Int
): Serializable

data class Clip(
        var clip: String
): Serializable

data class Clips(
        //var 320: String,
        //var 640: String,
        var full: String
): Serializable
//GAME CLASS END


//MOVIE CLASS BEGIN
data class Movie(
        var id: Int,
        var preview: String,
        var data: MovieData
): Serializable

data class MovieData(
        var max: String
): Serializable
//MOVIE CLASS END


//SCREENSHOT CLASS START
data class Screenshot(
        var id: Int,
        var image: String,
        var width: Int,
        var height: Int
): Serializable
//SCREENSHOT CLASS END