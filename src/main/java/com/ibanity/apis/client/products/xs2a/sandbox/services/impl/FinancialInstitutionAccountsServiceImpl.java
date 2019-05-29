package com.ibanity.apis.client.products.xs2a.sandbox.services.impl;

import com.ibanity.apis.client.jsonapi.DataApiModel;
import com.ibanity.apis.client.jsonapi.RelationshipsApiModel;
import com.ibanity.apis.client.jsonapi.RequestApiModel;
import com.ibanity.apis.client.models.IbanityCollection;
import com.ibanity.apis.client.models.IbanityProduct;
import com.ibanity.apis.client.network.http.client.IbanityHttpClient;
import com.ibanity.apis.client.products.xs2a.models.FinancialInstitution;
import com.ibanity.apis.client.products.xs2a.sandbox.models.FinancialInstitutionAccount;
import com.ibanity.apis.client.products.xs2a.sandbox.models.FinancialInstitutionUser;
import com.ibanity.apis.client.products.xs2a.sandbox.models.factory.create.FinancialInstitutionAccountCreationQuery;
import com.ibanity.apis.client.products.xs2a.sandbox.models.factory.delete.FinancialInstitutionAccountDeleteQuery;
import com.ibanity.apis.client.products.xs2a.sandbox.models.factory.read.FinancialInstitutionAccountReadQuery;
import com.ibanity.apis.client.products.xs2a.sandbox.models.factory.read.FinancialInstitutionAccountsReadQuery;
import com.ibanity.apis.client.products.xs2a.sandbox.services.FinancialInstitutionAccountsService;
import com.ibanity.apis.client.services.ApiUrlProvider;

import java.util.function.Function;

import static com.ibanity.apis.client.mappers.IbanityModelMapper.buildRequest;
import static com.ibanity.apis.client.mappers.IbanityModelMapper.mapCollection;
import static com.ibanity.apis.client.mappers.IbanityModelMapper.mapResource;
import static com.ibanity.apis.client.mappers.IbanityModelMapper.toIbanityModel;
import static com.ibanity.apis.client.utils.URIHelper.buildUri;
import static org.apache.commons.lang3.StringUtils.removeEnd;

public class FinancialInstitutionAccountsServiceImpl implements FinancialInstitutionAccountsService {

    private final ApiUrlProvider apiUrlProvider;
    private final IbanityHttpClient ibanityHttpClient;

    public FinancialInstitutionAccountsServiceImpl(ApiUrlProvider apiUrlProvider, IbanityHttpClient ibanityHttpClient) {
        this.apiUrlProvider = apiUrlProvider;
        this.ibanityHttpClient = ibanityHttpClient;
    }

    @Override
    public FinancialInstitutionAccount find(FinancialInstitutionAccountReadQuery accountReadQuery) {
        String url =
                getUrl(accountReadQuery.getFinancialInstitutionId().toString(),
                        accountReadQuery.getFinancialInstitutionUserId().toString(),
                        accountReadQuery.getFinancialInstitutionAccountId().toString());

        String response = ibanityHttpClient.get(buildUri(url));
        return mapResource(response, responseMapping());
    }

    @Override
    public IbanityCollection<FinancialInstitutionAccount> list(FinancialInstitutionAccountsReadQuery accountsReadQuery) {
        String url =
                getUrl(accountsReadQuery.getFinancialInstitutionId().toString(),
                        accountsReadQuery.getFinancialInstitutionUserId().toString(),
                        "");

        String response = ibanityHttpClient.get(buildUri(url, accountsReadQuery.getPagingSpec()));
        return mapCollection(response, responseMapping());
    }

    @Override
    public FinancialInstitutionAccount delete(FinancialInstitutionAccountDeleteQuery accountDeleteQuery) {
        String url =
                getUrl(accountDeleteQuery.getFinancialInstitutionId().toString(),
                        accountDeleteQuery.getFinancialInstitutionUserId().toString(),
                        accountDeleteQuery.getFinancialInstitutionAccountId().toString());

        String response = ibanityHttpClient.delete(buildUri(url));
        return mapResource(response, FinancialInstitutionAccount.class);
    }

    @Override
    public FinancialInstitutionAccount create(FinancialInstitutionAccountCreationQuery query) {
        String url =
                getUrl(query.getFinancialInstitutionId().toString(),
                        query.getFinancialInstitutionUserId().toString(),
                        "");

        FinancialInstitutionAccount financialInstitutionAccount = mapRequest(query);

        RequestApiModel request = buildRequest(FinancialInstitutionAccount.RESOURCE_TYPE, financialInstitutionAccount);
        String response = ibanityHttpClient.post(buildUri(url), request);
        return mapResource(response, responseMapping());
    }

    private FinancialInstitutionAccount mapRequest(FinancialInstitutionAccountCreationQuery query) {
        FinancialInstitutionAccount financialInstitutionAccount = new FinancialInstitutionAccount();
        financialInstitutionAccount.setDescription(query.getDescription());
        financialInstitutionAccount.setReference(query.getReference());
        financialInstitutionAccount.setReferenceType(query.getReferenceType());
        financialInstitutionAccount.setAvailableBalance(query.getAvailableBalance());
        financialInstitutionAccount.setCurrentBalance(query.getCurrentBalance());
        financialInstitutionAccount.setCurrency(query.getCurrency());
        financialInstitutionAccount.setSubType(query.getSubType());
        return financialInstitutionAccount;
    }

    private String getUrl(String financialInstitutionId, String financialInstitutionUserId, String financialInstutionAccountId) {
        return removeEnd(apiUrlProvider.find(IbanityProduct.Xs2a, "sandbox", "financialInstitution", "financialInstitutionAccounts")
                .replace(FinancialInstitution.API_URL_TAG_ID, financialInstitutionId)
                .replace(FinancialInstitutionUser.API_URL_TAG_ID, financialInstitutionUserId)
                .replace(FinancialInstitutionAccount.API_URL_TAG_ID, financialInstutionAccountId), "/");
    }

    private Function<DataApiModel, FinancialInstitutionAccount> responseMapping() {
        return dataApiModel -> {
            RelationshipsApiModel financialInstitution = dataApiModel.getRelationships().get("financialInstitution");
            RelationshipsApiModel financialInstitutionUser = dataApiModel.getRelationships().get("financialInstitutionUser");
            FinancialInstitutionAccount financialInstitutionAccount = toIbanityModel(dataApiModel, FinancialInstitutionAccount.class);
            financialInstitutionAccount.setFinancialInstitutionId(financialInstitution.getData().getId());
            financialInstitutionAccount.setFinancialInstitutionUserId(financialInstitutionUser.getData().getId());
            return financialInstitutionAccount;
        };
    }
}