import renderer from 'react-test-renderer'
import ChannelsContainer from 'odin/containers/channelsContainer.js'

test('Channels container renders OK', () => {
  const component = renderer.create(
    <ChannelsContainer schema={this.props.schema} project={this.state.project}/>
  )
  let tree = component.toJSON()
  expect(tree).toMatchSnapshot()
})