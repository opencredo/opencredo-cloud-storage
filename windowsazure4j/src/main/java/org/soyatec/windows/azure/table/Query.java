/**
 * Copyright  2006-2009 Soyatec
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * $Id$
 */
package org.soyatec.windows.azure.table;

import java.util.ArrayList;
import java.util.List;

import org.soyatec.windows.azure.constants.QueryParams;

public class Query {

	private static final String AND = "and";
	private static final String OR = "or";
	private static final String NOT = "not";

	private int top = -1;
	private List<String> orderFields = new ArrayList<String>();
	private List<String> conditions = new ArrayList<String>();
	private List<Query> subQueries = new ArrayList<Query>();

	private String connector = AND;

	public static Query select() {
		return new Query();
	}
	
	public static Query $() {
		return new Query();
	}
	
//	public static Query $orderAsc(String field) {
//		Query query = new Query();		
//		return query.orderAsc(field);
//	}

	public Query orderAsc(String field) {
		orderFields.add(field + " asc");
		return this;
	}

	public Query orderDesc(String field) {
		orderFields.add(field + " desc");
		return this;
	}

	public Query partitionKeyEq(String value) {
		conditions.add("PartitionKey eq " + quote(value));
		return this;
	}

	public Query where(String condition) {
		conditions.add(condition);
		return this;
	}

	public Query rowKeyEq(String value) {
		conditions.add("RowKey eq " + quote(value));
		return this;
	}

	public Query eq(String name, Object value) {
		conditions.add(name + " eq " + quote(value));
		return this;
	}

	public Query gt(String name, Object value) {
		conditions.add(name + " gt " + quote(value));
		return this;
	}

	public Query lt(String name, Object value) {
		conditions.add(name + " lt " + quote(value));
		return this;
	}

	public Query ge(String name, Object value) {
		conditions.add(name + " ge " + quote(value));
		return this;
	}

	public Query le(String name, Object value) {
		conditions.add(name + " le " + quote(value));
		return this;
	}

	public Query ne(String name, Object value) {
		conditions.add(name + " ne " + quote(value));
		return this;
	}

	public Query top(int t) {
		this.top = t;
		return this;
	}

	private String quote(Object value) {
		if (value instanceof String)
			return "'" + value + "'";
		return value.toString();
	}

	public static Query and(Query q, Query query) {
		Query parent = Query.select();
		parent.connector = AND;
		parent.addQuery(q);
		parent.addQuery(query);
		return parent;
	}

	public static Query or(Query q, Query query) {
		Query parent = Query.select();
		parent.connector = OR;
		parent.addQuery(q);
		parent.addQuery(query);
		return parent;
	}

	public static Query not(Query query) {
		Query parent = Query.select();
		parent.connector = NOT;
		parent.addQuery(query);
		return parent;
	}

	public String toAzureQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append(getFilter());

		String orderBy = getOrderBy();
		if (sb.length() > 0 && orderBy.length() > 0)
			sb.append("&");

		sb.append(orderBy);

		if (top != -1) {
			if (sb.length() > 0)
				sb.append("&");
			sb.append(QueryParams.QueryParamTableTopPrefix + "=" + top);
		}
		return sb.toString();
	}

	private String getFilter() {
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < subQueries.size(); i++) {
			if (!first)
				sb.append(" " + connector + " ");
			String query = subQueries.get(i).toAzureQuery();
			if (query.startsWith(QueryParams.QueryParamTableFilterPrefix)) {
				query = query.substring(QueryParams.QueryParamTableFilterPrefix
						.length() + 1);
			}
			sb.append("(").append(query).append(")");
			first = false;
		}

		for (int i = 0; i < conditions.size(); i++) {
			if (!first)
				sb.append(" " + connector + " ");

			sb.append("(").append(conditions.get(i)).append(")");
			first = false;
		}
		String filter = sb.toString();
		if (filter.length() > 0)
			filter = QueryParams.QueryParamTableFilterPrefix + "=" + filter;
		return filter;
	}

	private String getOrderBy() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < orderFields.size(); i++) {
			if (i > 0)
				sb.append(",");

			sb.append(orderFields.get(i));
		}
		String orderBy = sb.toString();
		if (orderBy.length() > 0)
			orderBy = QueryParams.QueryParamTableOrderbyPrefix + "=" + orderBy;

		return orderBy;
	}

	private void addQuery(Query query) {
		subQueries.add(query);
		this.orderFields.addAll(query.orderFields);
		query.orderFields.clear();
		if (top == -1) {
			top = query.top;
		}
		query.top = -1;
	}

}
