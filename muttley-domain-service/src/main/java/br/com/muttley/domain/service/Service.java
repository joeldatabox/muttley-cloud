package br.com.muttley.domain.service;

import br.com.muttley.model.security.model.User;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Service<T, ID extends Serializable> {
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
    T save(User user, T value);

    /**
     * Este método é sempre chamado antes de persistir algum registro no banco de dados.
     * Caso queira realizar algum tipo de validação antes de salvar algo, sobrescreva esse método
     * com sua regra de negócio jutamente com suas exceptions.
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */
    void checkPrecondictionSave(User user, T value);

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
    T update(User user, T value);

    /**
     * Este método é sempre chamado antes de persistir a atualização de algum registro no banco de dados.
     * Caso queira realizar algum tipo de validação antes de atualizar algo, sobrescreva esse método
     * com sua regra de negócio jutamente com suas exceptions.
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser atualizado
     */
    void checkPrecondictionUpdate(User user, T value);

    /**
     * Busca um registro pelo id
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id procurado
     */
    T findById(User user, ID id);

    /**
     * Pega o primeiro registro que encontrar
     *
     * @param user -> usuário da requisição corrente
     */
    T findFirst(User user);

    /**
     * Deleta um registro pelo id
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id procurado
     */
    void deleteById(User user, ID id);

    /**
     * Deleta um registro qualquer. Antes de se deletar qualquer registro, o método
     * <b>checkPrecondictionDelete<b/> é chamado para executar devidas validações
     * para se deletar o registro.
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser deletado
     */
    void delete(User user, T value);

    /**
     * Qualquer regra de négocio que valide o processo de delete deve ser implementada
     * nesse método através de sobrescrita
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id do registro a ser deletado
     */
    void checkPrecondictionDelete(User user, ID id);

    /**
     * Executa apos a deleção de um registro
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro que foi deletado
     */
    void beforeDelete(User user, T value);

    /**
     * Executa apos a deleção de um registro
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id do registro que foi deletado
     */
    void beforeDelete(User user, ID id);

    /**
     * Realiza o processo de count com base nos critérios
     * recebidos como parâmetros;
     *
     * @param user             -> usuário da requisição corrente
     * @param allRequestParams -> Todos os parametros passado na query da requisição
     */
    Long count(User user, Map<String, Object> allRequestParams);

    /**
     * Realiza o processo de listagem com base nos critérios
     * recebidos como parâmetros;
     *
     * @param user             -> usuário da requisição corrente
     * @param allRequestParams -> Todos os parametros passado na query da requisição
     */
    List<T> findAll(User user, Map<String, Object> allRequestParams);
}
