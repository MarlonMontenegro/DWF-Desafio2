package sv.edu.udb.www.desafio2.managedbeans;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.RequestScoped;
import jakarta.faces.context.FacesContext;
import sv.edu.udb.www.desafio2.entities.Author;
import sv.edu.udb.www.desafio2.entities.LiteraryGenre;
import sv.edu.udb.www.desafio2.models.AuthorModel;
import sv.edu.udb.www.desafio2.models.LiteraryGenreModel;
import sv.edu.udb.www.desafio2.utils.JsfUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ManagedBean
@RequestScoped
public class AuthorBean {

    private final AuthorModel authorModel = new AuthorModel();
    private final LiteraryGenreModel genreModel = new LiteraryGenreModel();
    private boolean editMode = false;

    private Author author = new Author();
    private Integer generoSeleccionado;
    private String filtroNombre;

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public Integer getGeneroSeleccionado() { return generoSeleccionado; }
    public void setGeneroSeleccionado(Integer generoSeleccionado) { this.generoSeleccionado = generoSeleccionado; }

    public String getFiltroNombre() { return filtroNombre; }
    public void setFiltroNombre(String filtroNombre) { this.filtroNombre = filtroNombre; }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }


    public List<LiteraryGenre> getGeneros() {
        return genreModel.listarGeneros();
    }


    public List<Author> getListaAutores() {
        List<Author> base = (generoSeleccionado == null)
                ? authorModel.listarAutores()
                : authorModel.listarPorGenero(generoSeleccionado);

        if (filtroNombre != null && !filtroNombre.isBlank()) {
            return authorModel.listarFiltradoPorNombre(filtroNombre);
        }
        return base;
    }

    public int getConteoVisibles() {
        return getListaAutores().size();
    }

    public String guardarAutor() {

        if (author.getFullName() == null || author.getFullName().isBlank()) {
            JsfUtil.setErrorMessage(null, "El nombre es obligatorio.");
            return null;
        }

        LocalDate hoy = LocalDate.now();
        if (author.getBirthDate() == null || author.getBirthDate().isAfter(hoy)) {
            JsfUtil.setErrorMessage(null, "La fecha de nacimiento es obligatoria y no puede ser futura.");
            return null;
        }


        if (generoSeleccionado == null) {
            JsfUtil.setErrorMessage(null, "Debe seleccionar un género literario.");
            return null;
        }

        LiteraryGenre genero = genreModel.buscarPorId(generoSeleccionado);
        author.setLiteraryGenre(genero);

        if (author.getCreatedAt() == null) {
            author.setCreatedAt(LocalDateTime.now());
        }

        boolean duplicado = authorModel.existeAutorPorNombreYFecha(author.getFullName(), author.getBirthDate());

        int filas = (author.getId() == null)
                ? authorModel.insertarAutor(author)
                : authorModel.actualizarAutor(author);

        if (filas != 1) {
            JsfUtil.setErrorMessage(null, "No se pudo guardar el autor.");
            return null;
        }

        if (duplicado) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Autor duplicado",
                            "Este autor ya había sido agregado anteriormente."));
        } else {
            JsfUtil.setFlashMessage("exito", (author.getId() == null)
                    ? "Autor registrado exitosamente."
                    : "Autor actualizado correctamente.");
        }

        return "autores?faces-redirect=true";
    }

    public String eliminarAutor() {
        int filas = authorModel.eliminarAutor(author.getId());
        if (filas != 1) {
            JsfUtil.setErrorMessage(null, "No se pudo eliminar el autor.");
            return null;
        }
        JsfUtil.setFlashMessage("exito", "Autor eliminado.");
        return "autores?faces-redirect=true";
    }

    public String editarAutor(Author a) {
        this.author = a;
        this.generoSeleccionado = (a.getLiteraryGenre() != null) ? a.getLiteraryGenre().getId() : null;
        this.editMode = true;
        return null;
    }

    public void nuevoAutor() {
        this.author = new Author();
        this.generoSeleccionado = null;
        this.editMode = false;
    }
}
