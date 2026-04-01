import { Note } from "./Note";

export class NoteList {

  static notes: Note[] = []

  static createNote = (title: string) => {
    const id: string = Date.now().toString(36) + Math.random().toString(36).slice(2)

    const newNote: Note = new Note(id, title)
    this.notes.unshift(newNote)

    this.createNoteCards()
  }

  static createNoteCards = () => {


    const noteList = document.querySelector(".note-list") as HTMLUListElement;
    const formItem = document.querySelector(".form-item") as HTMLLIElement;
    noteList.innerHTML = ""
    noteList.append(formItem)


    this.notes.forEach((note) => {

      const li = document.createElement("li");
      const input = document.createElement("input");
      const checkbox = document.createElement("input");

      input.classList.add("title");
      input.value = note.getTitle();

      checkbox.type = "checkbox";

      li.classList.add("note-card");
      li.append(input);
      li.append(checkbox);

      noteList.append(li);
    })
  }

  static updateEvents() {
    const noteCards = document.querySelectorAll(".note-card");
    const noteCardsArr = [...noteCards] as HTMLLIElement[];

    noteCardsArr.forEach((card, index: number) =>
      card.childNodes.forEach((input) => {
        const titleInput = card.childNodes[0] as HTMLInputElement;
        const checkboxInput = card.childNodes[1] as HTMLInputElement;

        input.addEventListener("input", () => {
          const note = this.notes[index];
          if (!note) return; // Si no existe la nota, salimos de la función

          note.setTitle(titleInput.value);
          note.setCompleted(checkboxInput.checked);
        });
      })
    );
  }

}