import Tooltip from 'tooltip.js'

export default {
    name: 'tooltip',
    directive: {
        bind(element, binding) {
            new Tooltip(element, {
                title: binding.value,
                trigger: 'hover',
                container: element
            })
        }

    }
}
