import Tooltip from 'tooltip.js'
import { each } from 'lodash';

let tooltips = [];

export default {
  name: 'tooltip',
  directive: {
    bind(element, binding) {
      element.addEventListener('mouseenter', () => {
        let tooltip = createTooltip(element, binding.value)
        tooltips.push(tooltip);
        tooltip.show();
      })

      element.addEventListener('mouseleave', () => {
        disposeTooltips(tooltips)
      })

    },
  }
}

const createTooltip = function (element, content) {
  return new Tooltip(element, {
    title: content,
    trigger: 'manual',
    container: element,
  })
}

const disposeTooltips = function (tooltips) {
  each(tooltips, tooltip => {
    tooltip.dispose()
  })
}
