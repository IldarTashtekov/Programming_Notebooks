// Tipos de datos en type script:

let cadena: string = "string"
let numero: number = 1
let bolean: boolean = !true
let objeto: object = {
    nombre: " ",
    activado: true
}

let numArray: number[] = [1, 2, 3, 4]

// prohibido esto amore lol
let detodo: any = 1
detodo = true
detodo = "aa"

//union de dos tipos wtf
let numstring: number | string = "fga"
numstring = 1

// crear tipo
type tipoMio = number | boolean
let numboleano: tipoMio = true

type persona = {
    nombre: string,
    edad: number,
}

let juanico: persona = { nombre: "juanico", edad: 33 }

let arrayDeAberranteTipado: (number | string | boolean)[] = [true, 22, "jamas deberia usar este tipo de arrays multitipo"]

// Literal
type coma = ","

let unaComa: coma = ","

// Interseccion
type documentacion = {
    dni: string
}

type perfilCompleto = persona & documentacion

let perfilJuanito: perfilCompleto = {
    nombre: juanico.nombre,
    edad: juanico.edad,
    dni: "11d1vb"
}

// Classes
class Movie {
    constructor(
        public title: string,
        private duration: number,
        readonly hasOscars: boolean
    ) { }

    getInfo(): string {
        return `Title ${this.title} - Duration ${this.duration} - Has Oscars ${this.hasOscars}}`
    }
}

const movie1 = new Movie("el señor de los anillos", 300, true)

// Herencia
class HorrorMovie extends Movie {
    constructor(
        title: string,
        duration: number,
        hasOscars: boolean,
        public hasJumpScares: boolean
    ) {
        super(title, duration, hasOscars)
    }
}

const horrorMovie1 = new HorrorMovie("Scream", 90, false, true)
console.log(horrorMovie1.getInfo)

// Interfaces y ejemplo de uso de generico
interface IDirector<T> { // Una convencion comun en TypeScript es poner I delante del nombre de una Interfaz
    name: string;
    age: number;
    data: T;
}

const director1: IDirector<string> = {
    name: "Peter Jackson",
    age: 60,
    data: "Fantasico director"
}

// Implementar Interfaces en Clases
interface IVideo<T> {
    title: string;
    director: IDirector<T>;
    getDuration(): number;
}

class Video implements IVideo<string> {
    constructor(
        public title: string,
        private duration: number,
        public director: IDirector<string>
    ) { }

    getDuration(): number {
        return this.duration
    }
}


// Elementos del DOM
const h1: HTMLHeadingElement | null = document.querySelector("h1") // optional
const title: HTMLHeadingElement = document.querySelector(".title") as HTMLHeadingElement; // inseguro, sin option
const username: HTMLInputElement = document.querySelector("#username") as HTMLInputElement

// HTML Eventos y Reactividad

const movieArray: Movie[] = [movie1, horrorMovie1];

const button = document.querySelector(".btn") as HTMLButtonElement;
const movieList = document.querySelector(".movie-list") as HTMLUListElement;

// Lambda que por cada movie crea una serie de elementos html
const addMovie = (movie: Movie) => {
    const li: HTMLLIElement = document.createElement("li");
    const h3: HTMLHeadingElement = document.createElement("h3");
    const h4: HTMLHeadingElement = document.createElement("h4");
    const p: HTMLParagraphElement = document.createElement("p")

    h3.classList.add("title");
    h3.textContent = movie.title

    h4.classList.add("duration")
    h4.textContent = `${movie.getInfo()}`

    // Valor ternario. booleano ? true : false
    const mensaje: string = movie.hasOscars ? "La película tiene Óscars" : "La película no tiene Óscars";
    p.textContent = mensaje

    // Creamos un li (list item)
    li.classList.add("movie-card");
    li.append(h3);
    li.append(h4);
    li.append(p);

    // Añadimos el li dentro el ul(unordered list)
    movieList.append(li)
}


let moviesShown: boolean = false
button.addEventListener("click", () => {

    if (moviesShown) {
        movieList.innerHTML = "";

    } else {
        movieArray.forEach((movie) => addMovie(movie));
        button.textContent = "HideMovies"

    }

    moviesShown = !moviesShown
})


