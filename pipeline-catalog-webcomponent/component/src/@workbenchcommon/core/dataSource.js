/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
===================================================================================
This Acumos software file is distributed by AT&T and Tech Mahindra
under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
This file is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
===============LICENSE_END=========================================================
*/

import { filter, orderBy, isFunction, each, lowerCase, isString} from "lodash-es";

export class DataSource {
  constructor({ data, page, pageSize, filter, sort }) {
    this._rawData = data;
    this._page = page || 0;
    this._pageSize = pageSize || 10;
    this._filter = filter;
    this._searchCriteria;
    this._sort = sort;
    this._cachedData;
  }

  get filtered() {
    if (this._fuzzyFilter) {
      let results = [];
      each(this._cachedData, item => {
        let key = Object.keys(this._filter)[0];
        if (lowerCase(item[key]).includes(this._filter[key])) {
          results.push(item);
        }
      });
      return results;
    } else {
      return filter(this._cachedData, this._filter);
    }
  }

  get sorted() {
    if (isFunction(this._sort)) {
      return this._cachedData.sort(this._sort);
    } else {
      return orderBy(this._cachedData, [field => {
        if (isString(field[this._sort])) {
          return field[this._sort].toLowerCase()
        }

        return field[this._sort];
      }], ['asc']);
    }
  }

  get paged() {
    return this._cachedData.slice(
      this._pageSize * this.page,
      this._pageSize * this.page + this._pageSize
    );
  }
  /**
   * Gets the data applying paging and filtering
   *
   * @return {Object[]} Sorted, paged, and filtered data
   */
  get data() {
    this._cachedData = this._rawData;
    this._cachedData = this.searched;
    this._cachedData = this.filtered;
    this._cachedData = this.sorted;
    this._cachedData = this.paged;

    return this._cachedData;
  }

  /**
   * Set a filter for the data
   *
   * @param {Object} filter A `{key: value} ` to filter by
   * @param {Boolean} fuzzy Perform a fuzzy filter rather than exact
   */
  filter(filter, fuzzy) {
    this._filter = filter;
    //this._filter = toLower(filter);
    this._fuzzyFilter = fuzzy || false;
  }

  search(searchCriteria) {
    this._searchCriteria = searchCriteria;
  }

  /**
   * Set a filter for the data
   *
   * @param {String} filter A `valye ` to filter by
   */
  get searched() {
    let results = [];
    if (this._searchCriteria) {
      results = this._cachedData.filter(item => {
        const filterTextLower = this._searchCriteria.toLowerCase();
        return JSON.stringify(item).toLowerCase().includes(filterTextLower);
      });
    } else {
      results = [...this._cachedData];
    }
    return results;
  }

  /**
   * Sets the sort strategy
   *
   * @param {String|Function} sort A key to sort by or sort function
   */
  sort(sort) {
    this._sort = sort;
  }

  /**
   * sets the current page
   *
   * @param {Number} page A page number
   */
  set page(page) {
    if (page >= 0 || page < this.totalPages) {
      this._page = page;

    }
  }

  get page() {
    return this._page;
  }

  /**
   * The number of records in the dataset
   *
   * @return {Number} The number of records in the dataset
   */
  total() {
    return this._rawData.length;
  }

  /**
   * The total number of pages
   *
   * @return {Number} The to total number of pages
   */
  get totalPages() {
    this._cachedData = this._rawData;

    return Math.ceil(this.filtered.length / this._pageSize);
  }

  /**
   * Navigates the dataset
   *
   * @param {String} direction Available Options "first", "next", "previous", "last"
   */
  navigatePage(direction) {
    switch (direction) {
      case "first":
        this.page = 0;
        break;
      case "next":
        this.page = ((this.totalPages - 1) > this.page) ? this.page + 1 : this.page;
        break;
      case "previous":
        this.page = (this.page > 0) ? this.page - 1 : this.page;
        break;
      case "last":
        this.page = this.totalPages - 1;
        break;
    }
  }
}
