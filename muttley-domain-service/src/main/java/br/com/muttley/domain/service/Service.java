package br.com.muttley.domain.service;

import br.com.muttley.model.Model;
import br.com.muttley.model.security.model.User;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public interface Service<T extends Model, ID extends Serializable> {
    /**
     * Salva um novo registro no banco de dados,
     * garantindo sempre que ele esteja relacionado a um usuário/owner.
     * <p>
     * Antes de ser salvo qualquer registro, primeiramente é executado a regra
     * de negócio presente no metodo <b>checkPrecondictionSave<b/>
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */
    T save(final User user, final T value);

    /**
     * Este método é sempre chamado antes de persistir algum registro no banco de dados.
     * Caso queira realizar algum tipo de validação antes de salvar algo, sobrescreva esse método
     * com sua regra de negócio jutamente com suas exceptions.
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */
    void checkPrecondictionSave(final User user, final T value);

    /**
     * Atualiza um novo registro no banco de dados,
     * garantindo sempre que ele esteja relacionado a um usuário/owner.
     * <p>
     * Antes de ser atualizado qualquer registro, primeiramente é executado a regra
     * de negócio presente no metodo <b>checkPrecondictionUpdate<b/>
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser atualizado
     */
    T update(final User user, final T value);

    /**
     * Este método é sempre chamado antes de persistir a atualização de algum registro no banco de dados.
     * Caso queira realizar algum tipo de validação antes de atualizar algo, sobrescreva esse método
     * com sua regra de negócio jutamente com suas exceptions.
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser atualizado
     */
    void checkPrecondictionUpdate(final User user, final T value);

    /**
     * Busca um registro pelo id
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id procurado
     */
    T findById(final User user, final ID id);

    /**
     * Pega o primeiro registro que encontrar
     *
     * @param user -> usuário da requisição corrente
     */
    T findFirst(final User user);

    /**
     * Deleta um registro pelo id
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id procurado
     */
    void deleteById(final User user, final ID id);

    /**
     * Deleta um registro qualquer. Antes de se deletar qualquer registro, o método
     * <b>checkPrecondictionDelete<b/> é chamado para executar devidas validações
     * para se deletar o registro.
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser deletado
     */
    void delete(final User user, final T value);

    /**
     * Qualquer regra de négocio que valide o processo de delete deve ser implementada
     * nesse método através de sobrescrita
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id do registro a ser deletado
     */
    void checkPrecondictionDelete(final User user, final ID id);

    /**
     * Realiza o processo de count com base nos critérios
     * recebidos como parâmetros;
     *
     * @param user             -> usuário da requisição corrente
     * @param allRequestParams -> Todos os parametros passado na query da requisição
     */
    Long count(final User user, final Map<String, Object> allRequestParams);

    /**
     * Realiza o processo de listagem com base nos critérios
     * recebidos como parâmetros;
     *
     * @param user             -> usuário da requisição corrente
     * @param allRequestParams -> Todos os parametros passado na query da requisição
     */
    List<T> findAll(final User user, final Map<String, Object> allRequestParams);
}
