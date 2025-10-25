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

    private Author author = new Author();
    private Integer generoSeleccionado;
    private String filtroNombre;

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public Integer getGeneroSeleccionado() { return generoSeleccionado; }
    public void setGeneroSeleccionado(Integer generoSeleccionado) { this.generoSeleccionado = generoSeleccionado; }

    public String getFiltroNombre() { return filtroNombre; }
    public void setFiltroNombre(String filtroNombre) { this.filtroNombre = filtroNombre; }


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


        boolean duplicado = authorModel.existeAutorPorNombreYFecha(author.getFullName(), author.getBirthDate());
        if (author.getCreatedAt() == null) author.setCreatedAt(LocalDateTime.now());


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
                    ? "Autor registrado exitosamente." : "Autor actualizado correctamente.");
        }

        return "autores?faces-redirect=true";
    }

    public String eliminarAutor(Author a) {
        int filas = authorModel.eliminarAutor(a.getId());
        if (filas != 1) {
            JsfUtil.setErrorMessage(null, "No se pudo eliminar el autor.");
            return null;
        }
        JsfUtil.setFlashMessage("exito", "Autor eliminado.");
        return "autores?faces-redirect=true";
    }

    public String editarAutor(Author a) {
        this.author = a;
        if (a.getLiteraryGenre() != null) this.generoSeleccionado = a.getLiteraryGenre().getId();
        return "registroAutores?faces-redirect=true";
    }
}
