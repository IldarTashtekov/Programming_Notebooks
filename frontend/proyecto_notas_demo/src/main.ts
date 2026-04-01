import { NoteList } from "./clases/NoteList";

const noteForm: HTMLFormElement = document.querySelector(".note-form") as HTMLFormElement;

noteForm.addEventListener("submit", (e: SubmitEvent) => {
    e.preventDefault();

    const form = e.target as HTMLFormElement;
    const input = form.elements[0] as HTMLInputElement;


    NoteList.createNote(input.value);
    input.value = ""
})