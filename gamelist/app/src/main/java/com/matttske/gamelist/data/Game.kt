package com.matttske.gamelist.data

class Game(var id: Int, var slug: String, var name: String, var released: String, var tba: Boolean, var background_image: String, var rating: String, var metacritic: String) {

}

/*
"id": 0,
"slug": "string",
"name": "string",
"released": "2020-12-18",
"tba": true,
"background_image": "http://example.com",
"rating": 0,
"rating_top": 0,
"ratings": { },
"ratings_count": 0,
"reviews_text_count": "string",
"added": 0,
"added_by_status": { },
"metacritic": 0,
"playtime": 0,
"suggestions_count": 0,
"updated": "2020-12-18T22:08:42Z",
"esrb_rating": {
"id": 0,
"slug": "everyone",
"name": "Everyone"
},
"platforms": [
{
"platform": {
"id": 0,
"slug": "string",
"name": "string"
},
"released_at": "string",
"requirements": {
"minimum": "string",
"recommended": "string"
}
}
]
*/