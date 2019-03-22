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

import { filter, sortBy, isFunction, each, lowerCase } from "lodash-es";


export class DataSource {
  constructor({ data, page, pageSize, filter, sort }) {
    this._rawData = data;
    this._page = page || 0;
    this._pageSize = pageSize || 10;
    this._filter = filter;
    this._sort = sort;
  }

  /**
   * Gets the data applying paging and filtering
   * 
   * @return {Object[]} Sorted, paged, and filtered data
   */
  data() {
    let sortedData, pagedData, filteredData;

    if (isFunction(this._sort)) {
      sortedData = sortBy(this._rawData, this._sort);
    } else {
      sortedData = sortBy(this._rawData, [this._sort]);
    }

    pagedData = sortedData.slice(
      this._pageSize * this._page,
      this._pageSize * this._page + this._pageSize
    );

    if (this._fuzzyFilter) {
      let results = [];

      each(this._rawData, item => {
        let key = Object.keys(this._filter)[0];

        if (lowerCase(item[key]).includes(this._filter[key])) {
          results.push(item);
        }
      });

      filteredData = results;
    } else {
      filteredData = filter(pagedData, this._filter);
    }

    return filteredData;
  }

  /**
   * Set a filter for the data
   * 
   * @param {Object} filter A `{key: value} ` to filter by
   * @param {Boolean} fuzzy Perform a fuzzy filter rather than exact
   */
  filter(filter, fuzzy) {
    this._filter = filter;
    this._fuzzyFilter = fuzzy || false;
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
   * Gets or sets the current page
   * 
   * @param {Number} page A page number
   */
  page(page) {
    if (typeof page !== "undefined") {
      // Bounds check
      if (page < 0 || page > this.totalPages() - 1) {
        return;
      }
      this._page = page;
      return;
    }

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
  totalPages() {
    return Math.floor(this._rawData.length / this._pageSize);
  }

  /**
   * Navigates the dataset
   * 
   * @param {String} direction Available Options "first", "next", "previous", "last"
   */
  navigatePage(direction) {
    let currentPageIndex = this._page;

    switch (direction) {
      case "first":
        this.page(0);
        break;
      case "next":
        this.page(currentPageIndex + 1);
        break;
      case "previous":
        this.page(currentPageIndex - 1);
        break;
      case "last":
        this.page(this.totalPages() - 1);
        break;
    }
  }
}
