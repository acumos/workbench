<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <ToastUI
      id="dataset-form"
      class="relative p-2"
      innerClass="w-full"
    ></ToastUI>
    <div class="p-3" style="overflow-y: scroll; height: 300px;">
      <div class="flex mb-2">
        <label class="mt-2">Data Source
        </label>
        <hr>
      </div>
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">
            Datasource Name:
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="Dataset name"
            rules="required|startAlpha"
            v-slot="{ errors, classes }"
          >
            <input
              type="text"
              class="form-input"
              :class="classes"
              v-model="updatedDataset.name"
              placeholder="Enter Datasource Name"
            />
            <span
              class="text-sm text-red-700 flex items-center"
              v-if="errors[0]"
            >
              <FAIcon icon="exclamation-triangle" />
              <span class="ml-1 my-1">{{ errors[0] }}</span>
            </span>
          </ValidationProvider>
        </div>
      </div>
      <div class="flex flex-col">
        <label class="mt-2">Datasource Description
          <span class="text-red-500">*</span></label>
          <ValidationProvider
              class="flex flex-col"
              name="Dataset Description"
              rules="required"
              v-slot="{ errors, classes }"
            >
        <textarea
          class="form-textarea"
          rows="4"
          maxlength="1000"
          v-model="updatedDataset.datasourceDescription"
          placeholder="Enter Datasource Description"
        ></textarea>
        <span class="leading-none text-right text-gray-600 mt-1"
          >{{ 1000 - updatedDataset.datasourceDescription.length }} Chars</span
        >
        <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
      </div>
       <div class="flex mb-2">
		 <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Data Connector
			<span class="text-red-500">*</span></label>
            <ValidationProvider
              class="flex flex-col"
              name="Data Connector"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <select class="form-select" v-model="category">
                <option value>Select</option>
                <option value="mongo" disabled>Mongo</option>
                <option value="mysql">MySQL</option>
                <option value="couch">Couch</option>
              </select>
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div> 
        <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Read Write Descriptor
			<span class="text-red-500">*</span></label>
            <ValidationProvider
              class="flex flex-col"
              name="Read Write Descriptor"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <select class="form-select" v-model="readWriteDescriptor">
                <option value>Select</option>
                <option value="all" disabled>All</option>
                <option value="read">Read Only</option>
                <option value="write">Write Only</option>
              </select>
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
        </div> 

         <div class="flex mb-2">
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">
              Database Server User Name
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Database Server User Name"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <input
                type="text"
                class="form-input"
                v-model="updatedDataset.dbServerUsername"
                placeholder="Enter Database Server User Name"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">
              Database Server Password
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Database Server Password"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <input
                type="password"
                class="form-input"
                v-model="updatedDataset.dbServerPassword"
                placeholder="Enter Database Server Password"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
		        <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">
              Database Name
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Database Name"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <input
                type="text"
                class="form-input"
                v-model="updatedDataset.databaseName"
                placeholder="Enter Database Name"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
        </div> 

      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">
            JDBC URL:
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="JDBC URL"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <input
              type="text"
              class="form-input"
              :class="classes"
              placeholder="Enter JDBC URL"
              v-model="jdbcURL"
            />
            <span
              class="text-sm text-red-700 flex items-center"
              v-if="errors[0]"
            >
              <FAIcon icon="exclamation-triangle" />
              <span class="ml-1 my-1">{{ errors[0] }}</span>
            </span>
          </ValidationProvider>
        </div>
      </div>
      <div class="flex flex-col">
        <label class="mt-2">Database Query
         <span class="text-red-500">*</span>
         </label>
          <ValidationProvider
              class="flex flex-col"
              name="Database Query"
              rules="required"
              v-slot="{ errors, classes }"
            >
        <textarea
          class="form-textarea"
          rows="4"
          maxlength="1000"
          v-model="updatedDataset.dbQuery"
          placeholder="Enter Database Query"
        ></textarea>
        <span class="leading-none text-right text-gray-600 mt-1"
          >{{ 1000 - updatedDataset.dbQuery.length }} Chars</span
        >
        <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
      </div> 

        <div class="flex mb-2">
        <label class="mt-2">Dataource Metadata
        </label>
        <hr>
      </div>
      <div class="flex mb-2">
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Area </label>
              <input
                type="text"
                class="form-input"
                v-model="area"
                placeholder="Enter Area"
              />
          </div>
          
           <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Datasource Type
            <ValidationProvider
              class="flex flex-col"
              name="Datasource Type"
              rules=""
              v-slot="{ errors, classes }"
            >
              <select class="form-select" v-model="datasetType">
                <option value>Select</option>
              </select>
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div> 
		         <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Task Type</label>
              <select class="form-select" v-model="taskType">
                <option value>Select</option>
              </select>
          </div> 
        </div> 

     <div class="flex mb-2">
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Attribute Type</label>
              <select class="form-select" v-model="attributeType">
                <option value>Select</option>
              </select>
          </div> 
           <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Geo-Statistical Type</label>
              <select class="form-select" v-model="geoType">
                <option value>Select</option>
              </select>
          </div> 
		         <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Format Type</label>
              <select class="form-select" v-model="formatType">
                <option value>Select</option>
              </select>
          </div> 
        </div> 

       <div class="flex mb-2">
        <label class="mt-2">Custom Metadata</label><hr>
      </div>
        <!--<div class="flex flex-col">
 <vue-good-table
              v-if="!isEmpty"
              :columns="columns"
              :rows="notebooks"
              :line-numbers="true"
              :pagination-options="{ enabled: true, perPage: 5 }"
              :search-options="{ enabled: true, externalQuery: searchTerm }"
              :sort-options="sortOptions"
            >
              <template slot="table-row" slot-scope="props">
                <div class="flex justify-center" v-if="props.column.field === 'key'">
                    <FAIcon
                    class="text-gray-500"
                     icon="minus"
                    v-if="props.row.key=== ''"
                  ></FAIcon>
                </div>
              <div class="flex justify-center" v-if="props.column.field === 'actions'">
                     <button
                  class="btn btn-sm btn-primary text-white mr-2"
                  @click="editNotebook()"
                  :disabled="!loginAsOwner"
                  title="Create Notebook"
                >
                  <FAIcon icon="plus-square" />
                </button>
              </div>
              <div class="flex justify-center" v-if="props.column.field === 'value'">
                    <FAIcon
                    class="text-gray-500"
                     icon="minus"
                    v-if="props.row.value=== ''"
                  ></FAIcon>
                </div>
                 <div
                v-else-if="props.column.field === 'key'"
                  class="break-all justify-center px-1"
                >{{ props.formattedRow[props.column.field] }}
                </div> 
              </template> 
            </vue-good-table> 
          </div>-->
      

		  
    </div>
    <div
      class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(updatedDataset)">
        {{ isNew ? "Create" : "Save" }} Datasource
      </button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined } from "lodash-es";
import ToastUI from "../../../vue-common/components/ui/Toast.ui";
import { mapActions } from "vuex";
import Dataset from '../../../store/entities/datasource.entity';

export default {
  components: { ToastUI },
  // props: {
  //   data: {
  //     type: Object
  //   }
  // },
  data() {
    return {
      // columns: [   
      //   {
      //     label: "Actions",
      //     field: "actions"
      //   },
      //     {
      //     label: "Key",
      //     field: "key"
      //   },
      //   {
      //     label: "Value",
      //     field: "value"
      //   },
      // ],
      dataset: new Dataset(),
      category: "",
      readWriteDescriptor: "",
      datasetType: "",
      taskType: "",
      attributeType: "",
      geoType: "",
      formatType: "",
    };
  },
  created() {
   this.updatedDataset =  new Dataset(this.updatedDataset) ;
  },
  computed: {
    isNew() {
      return isUndefined(this.data);
    }
  },
  methods: {
    ...mapActions("datasource", ["createDataset", "allDatasets"]),
    ...mapActions("app", ["showToastMessage"]),
    async save(dataset) {
      const isValid = await this.$refs.form.validate();
      let response = null;
        if (isValid) {
         this.updatedDataset.readWriteDescriptor = this.readWriteDescriptor;
         this.updatedDataset.category = this.category;
         if((this.jdbcURL).split(':')){
            this.updatedDataset.serverName = (this.jdbcURL).substr(0,(this.jdbcURL).lastIndexOf(':'));
            this.updatedDataset.portNumber  = (this.jdbcURL).substr((this.jdbcURL).lastIndexOf(':')+1);
         }
        if (this.isNew) {
          dataset = new Dataset(this.updatedDataset);
          const datasetCreation = dataset.$toJson();
          response = await this.createDataset(datasetCreation);
          if (response.data.status === "Success") {
            await this.allDatasets();
            this.$emit("onSuccess");
            this.reset();
            this.showToastMessage({
              id: "global",
              message: `${response.data.message}`,
              type: "success"
            });
          } else {
            this.showToastMessage({
              id: "dataset-form",
              message: `${response.data.message}`,
              type: "error"
            });
          }
        }
      }
        
    },
    reset() {
      this.updatedDataset = new Dataset();
      this.category= "";
      this.jdbcURL = "";
      this.readWriteDescriptor="";
      this.$refs.form.reset();
    }
  }
};
</script>
